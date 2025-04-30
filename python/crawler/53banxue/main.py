# coding:utf-8
import re
import sys
import threading
from pathlib import Path

from python.utils import http_req


def req_53banxue(path: str, payload: dict = None, base_url: str = "https://gateway.53zaixian.com/resource-bs-api/res",
                 max_retry=60, retry_interval=5, save_path=None, chunk_size=128):
    url = path if save_path else f"{base_url}/{path.lstrip("/")}"
    result = http_req(url, payload, method="get" if save_path else "post", max_retry=max_retry,
                      retry_interval=retry_interval, save_path=save_path, chunk_size=chunk_size)
    return result


################################### 53banxue http api ########################################
def find_learning_period_grade():
    resp = req_53banxue("home/findLearningPeriodGrade")
    if resp and resp["code"] == "200":
        data = resp["data"]
        return data["learningPeriods"], data["semesterList"]
    return [], []


def recommend_book_page(grade_id: str, semester_id: str, subject_id: str = ""):
    payload = {
        "gradeId": grade_id,
        "semesterId": semester_id,
        "subjectId": subject_id,
        "versionId": "",
        # "page": 1,
        # "size": 20
    }
    resp = req_53banxue("home/recommendBookPage", payload)
    return resp["data"]["versionBooks" if subject_id else "subject"]


def get_download_book_zip_info(book_id: str):
    payload = {
        "bookId": book_id,
        "fileId": "",
        "packageId": ""
    }
    resp = req_53banxue("bs/getDownloadBookZipInfo", payload)
    if resp and resp["code"] == "200":
        data = resp["data"]
        zip_download_url = data["zipDownloadUrl"]
        zip_file_name = re.search("([^/]+\\.zip)", zip_download_url).group(1) if zip_download_url else None
        img_download_url = data["imgPath"]
        img_file_name = re.search("([^/]+)\\?", img_download_url).group(1) if img_download_url else None
        return zip_file_name, zip_download_url, img_file_name, img_download_url
    return None, None, None, None


def e_book_list(book_id: str):
    payload = {
        "bookId": book_id
    }
    resp = req_53banxue("bs/eBookList", payload)
    if resp and resp["code"] == "200":
        e_book_infos = resp["data"]["ebookInfos"]
        return [{
            "name": info["name"],
            "url": info["url"],
            "shelf_id": re.search("id=(.*)&", info["url"]).group(1),
        } for info in e_book_infos]
    return []


def e_book_detail(shelf_id: str):
    payload = {
        "shelf_id": shelf_id
    }
    resp = req_53banxue("ebook/book/detail", payload, base_url="https://res.53zaixian.com/api")
    if resp and resp["code"] == "200":
        book = resp["data"]["book"]
        if type(book) != dict:
            return None, []
        book_name = book["book_name"]
        book_res = book["book_res"]
        return book_name, [{
            "res_id": page["res_id"],
            "img_thumb_url": page["img_thumb_url"],
            "img_name": re.search("([^/]+$)", page["img_thumb_url"]).group(1),
        } for page in book_res]
    return None, []


################################### 53banxue parse ########################################
def download_ebook(book_id, save_dir):
    for book_info in e_book_list(book_id):
        book_name, book_pages = e_book_detail(book_info["shelf_id"])
        base_path = save_dir
        if book_name:
            base_path = f"{save_dir}/{book_name}"
            Path(base_path).mkdir(parents=True, exist_ok=True)
            print(f"{book_name} is downloading...")
        i = 0
        for b_page in book_pages:
            i += 1
            save_path = f"{base_path}/{b_page["res_id"]}_{b_page["img_name"]}"
            req_53banxue(b_page["img_thumb_url"], save_path=save_path)
            print(f"\tpage {i} is saved to {save_path}")


def download_book(book_id, save_dir):
    zip_file_name, zip_download_url, img_file_name, img_download_url = get_download_book_zip_info(book_id)
    if img_download_url:
        req_53banxue(img_download_url, save_path=f"{save_dir}/{img_file_name}")
        print(f"{img_file_name} is saved to {save_dir}")
    if zip_download_url:
        req_53banxue(zip_download_url, save_path=f"{save_dir}/{zip_file_name}")
        print(f"{zip_file_name} is saved to {save_dir}")


def get_books_by_grade(grade_id: str, semester_id: str, base_path: str, only_ebook=False):
    subjects = recommend_book_page(grade_id, semester_id)
    for subject in subjects:
        subject_id, subject_name = subject["id"], subject["name"]
        # print(f"\t\t\t{subject_name}")
        v_books = recommend_book_page(grade_id, semester_id, subject_id)
        for v_book in v_books:
            v_id, v_name, book_infos = v_book["id"], v_book["name"], v_book["bookInfos"]
            # print(f"\t\t\t\t{v_name}")
            save_dir = f"{base_path}/{subject_name}/{v_name}"
            if not only_ebook:
                Path(f"{save_dir}").mkdir(parents=True, exist_ok=True)
            for book_info in book_infos:
                b_id, b_assemble_name = book_info["id"], book_info["assembleName"]
                # print(f"\t\t\t\t\t{b_id}@{b_assemble_name}: {zip_download_url}")
                download_ebook(b_id, save_dir)
                if not only_ebook:
                    download_book(b_id, save_dir)


def main(base_dir, filter_period=None, filter_grade=None, multi_thread=False, only_ebook=False):
    learning_periods, semester_list = find_learning_period_grade()
    threads = []
    for learning_period in learning_periods:
        p_id, p_name = learning_period["id"], learning_period["name"]
        # print(p_name)
        if filter_period and filter_period(p_name):
            continue
        for grade in learning_period["gradeList"]:
            g_id, g_name = grade["id"], grade["name"]
            # print(f"\t{g_name}")
            if filter_grade and filter_grade(g_name):
                continue
            for semester in semester_list:
                s_id, s_name = semester["id"], semester["name"]
                # print(f"\t\t{s_name}")
                base_path = f"{base_dir}/{p_name}/{g_name}/{s_name}"
                if not multi_thread:
                    get_books_by_grade(g_id, s_id, base_path, only_ebook)
                    continue
                thread = threading.Thread(target=get_books_by_grade, args=(g_id, s_id, base_path, only_ebook))
                threads.append(thread)
                thread.start()
    for thread in threads:
        thread.join()


# ################################### 53banxue test ########################################
def api_test():
    book_id = "1481578009605177346"
    for book_info in e_book_list(book_id):
        book_name, book_pages = e_book_detail(book_info["shelf_id"])
        for book_page in book_pages:
            print(f"{book_page['res_id']}@{book_name}: {book_page["img_thumb_url"]}")
    zip_file_name, zip_download_url, _, _ = get_download_book_zip_info("1723228089669939201")
    print(zip_file_name, zip_download_url)


if __name__ == "__main__":
    if len(sys.argv) == 2 and sys.argv[1] == "test":
        api_test()
        exit(1)
    base_dir = "/Users/zhaohuiqiang/Documents/api_tester/53banxue_ebook" if len(sys.argv) < 2 else sys.argv[1]
    main(base_dir  # , filter_period=lambda p_name: p_name == "小学"
         # , filter_grade=lambda g_name: g_name == "七年级" or g_name == "八年级"
         , multi_thread=True, only_ebook=True)
