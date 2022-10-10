
echo y|pip3 uninstall bundcli
python3 setup.py sdist bdist_wheel
pip3 install dist/bundcli-1.0.0-py3-none-any.whl
bundcli create --help 