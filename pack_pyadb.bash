
echo y|pip3 uninstall pyadb
python3 setup.py sdist bdist_wheel
pip3 install dist/pyadb-1.0.0-py3-none-any.whl
pyadb device-info --help