## 包管理
```
##requirements 包管理
pip3 freeze > requirements.txt
pip3 install -r requirements.txt
##pipenv 包管理
pipenv install /  pipenv install -d --skip-lock
pipenv lock -r --dev > requirements.txt
pipenv install -r  requirements.txt
```

## 项目部署
1. docker build -t spacecraftserver4flask:default .
2. docker run -d -p 9000:9000 --name spacecraftserver4flask spacecraftserver4flask:default


## flask配置文件读取
- 配置文件config_file/config_object 总会被.flaskenv文件覆盖，.flaskenv
- pycharm 配置的env debug等优先级高与一切配置


