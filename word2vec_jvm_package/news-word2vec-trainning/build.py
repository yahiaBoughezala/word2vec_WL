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
    if len(sys.argv[1:]) == 3:
        print('Starting build  release for %s env %s name %s verson' % (sys.argv[1], sys.argv[2], sys.argv[3]))
    else:
        print("Usage : build.sh {stage|online} {news-word2vec-trainning|} {version} .eg: the parameter online news-word2vec-trainning 1.0")
        sys.exit()
# validate input parameter type
TARGET_ENV = sys.argv[1]
TARGET_NAME = sys.argv[2]
DEV = os.getcwd()
VERSION = sys.argv[3]
DISTRIB = DEV + '/build'

if TARGET_ENV != 'stage' and TARGET_ENV != 'online':
    print('Invalid env given. Usage : build.py {dev|test|stage|online} {news-word2vec-trainning|} {version}')
    sys.exit()

# =============================================
# some clean-up
# =============================================
print 'some clean-up DISTRIB is ' + DISTRIB
if (os.path.exists(DISTRIB)):
    rmtree(DISTRIB)
os.mkdir(DISTRIB)
DISTRIB = DISTRIB + '/' + VERSION
os.mkdir(DISTRIB)
print("The Final DISTRIB is: " + DISTRIB)  # D:\caishi\package\build\0.0.1

# =============================================
# create distrib lib dir
# =============================================
os.mkdir(DISTRIB + '/lib')
os.mkdir(DISTRIB+'/bin')
os.mkdir(DISTRIB + '/conf')

# =============================================
# scripts
# ============================================
server = DEV
copytree(server + "/bin/",DISTRIB+"/bin")
copytree(server + "/conf/",DISTRIB+"/conf")

os.system('mvn clean')
os.system('mvn package install -Dmaven.test.skip=true')

print(server + '/target/' + TARGET_NAME + '-' + VERSION + '-SNAPSHOT.jar')
print(DISTRIB + '/lib/' + TARGET_NAME + '-' + VERSION + '.jar')
copy(server + '/target/' + TARGET_NAME + '-1.0-SNAPSHOT.jar', DISTRIB + '/' + TARGET_NAME + '-' + VERSION + '.jar')

# =============================================
# lib dependency
# =============================================
os.system('mvn dependency:copy-dependencies')
copytree(server + '/target/dependency', DISTRIB + '/lib')

# =============================================
# make tar.gz
# =============================================
os.chdir(DISTRIB)
TAR_FILE_NAME = "spark-newsWord2VecTrainning"
tar = tarfile.open('' + TAR_FILE_NAME + '-' + VERSION + '.tar.gz', 'w:gz')
tar.add('bin')
tar.add('lib');
tar.add('conf')
tar.add('news-word2vec-trainning-1.0.jar')
tar.close()
