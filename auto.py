import os, shutil, subprocess
#empty the abi folder
folder = 'abi'
for the_file in os.listdir(folder):
    file_path = os.path.join(folder, the_file)
    try:
        if os.path.isfile(file_path):
            os.unlink(file_path)
        #elif os.path.isdir(file_path): shutil.rmtree(file_path)
    except Exception as e:
        print(e)
#run jar file
subprocess.call(['java', '-jar', 'jcadlibrun.jar'])
subprocess.call(['blender', '--python', 'blendit.py'])