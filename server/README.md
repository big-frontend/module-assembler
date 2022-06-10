## 包管理
```
##requirements 包管理
pip3 freeze > requirements.txt
pip3 install -r requirements.txt
##pipenv 包管理
pipenv install
pipenv lock -r --dev > requirements.txt
pipenv install -r  requirements.txt
```

##项目部署
1. docker build -t spacecraftserver4flask:default .
2. docker run -d -p 9000:9000 --name spacecraftserver4flask spacecraftserver4flask:default
