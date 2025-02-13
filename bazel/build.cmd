@echo off
setlocal enabledelayedexpansion

REM 获取脚本所在目录并切换
set "CD=%~dp0"
cd /d "%CD%"
echo Current Directory: %CD%

choco -y install bazelisk

REM 返回上级目录
cd ..

REM 设置构建参数（保持与Unix版本兼容）
set "OPTS=-c opt --cxxopt=-DENABLE_IPCL=false"

REM 执行Bazel查询并构建
bazel build %OPTS% //native/src/main/native/cn/cstn/algorithm/javacpp/heu/... //native/src/main/native/cn/cstn/algorithm/javacpp:func

endlocal