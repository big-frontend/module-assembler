# -*- coding: utf-8 -*-
from setuptools import find_packages, setup
# python setup.py install
setup(
    name='bundcli',
    version='1.0.0',
    packages=find_packages(include=['cmds']),
    include_package_data=True,
    zip_safe=False,
    install_requires=[
        'cmd-fwk', 'requests', 'tqdm'
    ],

    author="jamesfchen",
    author_email="hawksjamesf@gmail.com",
    description="bundles assembler cli",
    long_description='''bundles assembler cli''',
    long_description_content_type="text/markdown",
    license="Apache2",
    url='https://github.com/electrolyteJ/bundles-assembler/wiki',
    # keywords="",
    # url='',
    classifiers=[
        # How mature is this project? Common values are
        #   3 - Alpha
        #   4 - Beta
        #   5 - Production/Stable
        'Development Status :: 5 - Production/Stable',

        # Pick your license as you wish (should match "license" above)
        'License :: OSI Approved :: Apache Software License',

        # Specify the Python versions you support here. In particular, ensure
        # that you indicate whether you support Python 2, Python 3 or both.
        # 'Programming Language :: Python :: 2.7',
        'Programming Language :: Python :: 3',
        'Programming Language :: Python :: 3.3',
        'Programming Language :: Python :: 3.4',
        'Programming Language :: Python :: 3.5',
        'Programming Language :: Python :: 3.6',
        'Programming Language :: Python :: 3.7',
        'Programming Language :: Python :: 3.9',
    ],
    entry_points={
        'console_scripts': [
            'bundcli=cmds:entry',
        ],
    }
)
