import os, glob, shutil

os.chdir(os.path.dirname(__file__))


def do_collect():
    res = []
    for path in glob.glob('../build/libs/*.jar'):
        if any(x in path for x in ('source', 'dev', 'all')):
            continue
        print(path)
        shutil.copy(path, '.')
        res.append(path)
    return res


if __name__ == '__main__':
    do_collect()
