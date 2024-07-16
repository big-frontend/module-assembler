import subprocess,os,sys
def android():
    root_dir =  os.getcwd()
    gradlew = f'{root_dir}/android/gradlew'
    print(f"android {gradlew}")
    os.system(f'cd android && {gradlew} app:assemble')


def cli():
    os.system(f'cd cli  &&  poetry run macli {' '.join(sys.argv[1:])}')