# coding=utf8
# =============================================
# Develop tools: PyCharm Community Edition 2016.2
# Author: fuli.shen
# Date:2016
# =============================================
import os
import shutil
import sys
import tarfile
from shutil import rmtree, copy

# =============================================
# copy src to dst
# =============================================
def copytree(src, dst, symlinks=False, ignore=None):
    for item in os.listdir(src):
        s = os.path.join(src, item)
        d = os.path.join(dst, item)
        if os.path.isdir(s):
            shutil.copytree(s, d, symlinks, ignore)
        else:
            shutil.copy2(s, d)


if __name__ == '__main__':
    # validate input parameter
    if len(sys.argv[1:])==3:
        print('Starting build  release for %s name %s verson' % (sys.argv[1],sys.argv[2]))
    else:
        print("Usage : build.sh  {app_prefix} {version}  {env}  .eg: the parameter  oozie-training-word2vec 0.0.1 prod")
        sys.exit()
# validate input parameter type
TARGET_NAME= sys.argv[1]
DEV = os.getcwd()
VERSION = sys.argv[2]
ENV=sys.argv[3]
DISTRIB = DEV + '/build'

# =============================================
# some clean-up
# =============================================
print 'some clean-up DISTRIB is ' + DISTRIB
if (os.path.exists(DISTRIB)):
    rmtree(DISTRIB)
os.mkdir(DISTRIB)
DISTRIB = DISTRIB + '/' + VERSION
os.mkdir(DISTRIB)
print("The Final DISTRIB is: " + DISTRIB)

# =============================================
# create distrib lib dir
# =============================================
os.mkdir(DISTRIB + '/configs')
os.mkdir(DISTRIB + "/bin")
# =============================================
# scripts
# ============================================
server = DEV
copytree(server + "/bin/", DISTRIB+"/bin/")
copytree(server+'/configs/'+ ENV,DISTRIB +  '/configs/')

os.system('mvn clean')
os.system('mvn package install -Dmaven.test.skip=true')

print(server + '/target/'+TARGET_NAME+'-' + VERSION + '-SNAPSHOT.jar')

# =============================================
# make tar.gz
# =============================================
os.chdir(DISTRIB)
tar = tarfile.open(''+TARGET_NAME+'-' + VERSION + '.tar.gz', 'w:gz')
tar.add("bin")
tar.add('configs');
tar.close()