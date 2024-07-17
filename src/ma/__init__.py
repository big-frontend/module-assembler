import subprocess,os,sys
def android():
    root_dir =  os.getcwd()
    gradlew = f'{root_dir}/android/gradlew'
    print(f"android {gradlew}")
    os.system(f' cd android && {gradlew} app:assemble')


def cli():
    args = ' '.join(sys.argv[1:])
    os.system(f'poetry install && cd android && pwd &&  echo "sourceModules=" > local.properties && macli {args}')