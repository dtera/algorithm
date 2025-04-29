# coding:utf-8
import re
import threading
from pathlib import Path

from python.utils import http_req, download_url


def req_53banxue(path: str, payload: dict = None, max_retry=30, retry_interval=5):
    path = path.lstrip("/")
    url = f"https://gateway.53zaixian.com/resource-bs-api/res/{path}"
    result = http_req(url, payload, method="post", max_retry=max_retry, retry_interval=retry_interval)
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
        zip_file_name = re.sub(r"(.*/)", "", re.sub(r"(\?.*)", "", zip_download_url)) if zip_download_url else None
        img_download_url = data["imgPath"]
        img_file_name = re.sub(r"(.*/)", "", re.sub(r"(\?.*)", "", img_download_url)) if img_download_url else None
        return zip_file_name, zip_download_url, img_file_name, img_download_url
    return None, None, None, None


def get_books_by_grade(grade_id: str, semester_id: str, base_path: str):
    subjects = recommend_book_page(grade_id, semester_id)
    for subject in subjects:
        subject_id, subject_name = subject["id"], subject["name"]
        # print(f"\t\t\t{subject_name}")
        v_books = recommend_book_page(grade_id, semester_id, subject_id)
        for v_book in v_books:
            v_id, v_name, book_infos = v_book["id"], v_book["name"], v_book["bookInfos"]
            # print(f"\t\t\t\t{v_name}")
            save_dir = f"{base_path}/{subject_name}/{v_name}"
            Path(f"{save_dir}").mkdir(parents=True, exist_ok=True)
            for book_info in book_infos:
                b_id, b_assemble_name = book_info["id"], book_info["assembleName"]
                zip_file_name, zip_download_url, img_file_name, img_download_url = get_download_book_zip_info(b_id)
                if img_download_url:
                    download_url(img_download_url, save_path=f"{save_dir}/{img_file_name}")
                if zip_download_url:
                    download_url(zip_download_url, save_path=f"{save_dir}/{zip_file_name}")
                # print(f"\t\t\t\t\t{b_id}@{b_assemble_name}: {zip_download_url}")


def main(base_dir, multi_thread=True):
    learning_periods, semester_list = find_learning_period_grade()
    threads = []
    for learning_period in learning_periods:
        p_id, p_name = learning_period["id"], learning_period["name"]
        # print(p_name)
        if p_name == "小学":
            continue
        for grade in learning_period["gradeList"]:
            g_id, g_name = grade["id"], grade["name"]
            # print(f"\t{g_name}")
            for semester in semester_list:
                s_id, s_name = semester["id"], semester["name"]
                # print(f"\t\t{s_name}")
                base_path = f"{base_dir}/{p_name}/{g_name}/{s_name}"
                if not multi_thread:
                    get_books_by_grade(g_id, s_id, base_path)
                    continue
                thread = threading.Thread(target=get_books_by_grade, args=(g_id, s_id, base_path))
                threads.append(thread)
                thread.start()
    for thread in threads:
        thread.join()


if __name__ == "__main__":
    base_path = "/Users/zhaohuiqiang/Documents/api_tester/53banxue"
    main(base_path, multi_thread=False)
    # zip_file_name, zip_download_url, _, _ = get_download_book_zip_info("1481578009605177346")
    # print(zip_file_name, zip_download_url)
    # zip_file_name, zip_download_url, _, _ = get_download_book_zip_info("1723228089669939201")
    # print(zip_file_name, zip_download_url)
