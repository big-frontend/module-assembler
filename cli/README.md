
# 调试
- poetry install | poetry build | poetry run macli
- macli --help 

# 发布

poetry config pypi-token.pypi token

https://pyloong.github.io/pythonic-project-guidelines/guidelines/tutorial/publish/#_2

poetry build | poetry publish

# test 
- poetry install | poetry run pytest