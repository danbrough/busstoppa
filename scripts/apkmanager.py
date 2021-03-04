#!/usr/bin/env python3

import cmd
import os
import subprocess
import sys

PACKAGE = 'danbroid.busapp'
FLAVOUR = 'vtm'

HOME_DIR = os.path.abspath(os.path.join(os.path.dirname(os.path.realpath(os.path.abspath(sys.argv[0]))), '..'))
BACKUPS_DIR = os.path.join(HOME_DIR, 'data/backups')
RELEASE_APK =  os.path.join(HOME_DIR,'app/build/outputs/apk/vtm/release/app-vtm-release.apk')
os.chdir(HOME_DIR)

from termcolor import colored, cprint

# change to the busstoppa dir
os.chdir(os.path.join(os.path.dirname(os.path.realpath(os.path.abspath(sys.argv[0]))), '..'))


def log(msg):
    print(colored(msg, 'green', attrs=['bold']))


class Main(cmd.Cmd):
    prompt = colored(PACKAGE + '> ', 'green', attrs=['bold'])

    def print_prompt(self):
        self.stdout.write(self.prompt)
        self.stdout.flush()

    def do_build(self, line):
        log("do_build() %s" % line)
        line = line.split()
        password = ''
        if len(line) > 0:
            password = line[0]
        elif 'KEYSTORE_PASSWORD' in os.environ:
            password = os.environ['KEYSTORE_PASSWORD']
        else:
            self.print_line("No password provided")
            return
        cmd = './gradlew :app:assemble%sRelease -PKEYSTORE_PASSWORD=%s' % (FLAVOUR.title(), password)
        log('running ./gradlew :app:assemble%sRelease -PKEYSTORE_PASSWORD..' % (FLAVOUR.title()))
        os.system(cmd)

    def do_install(self, line):
        """Installs the latest release"""
        line = line.split()
        password = ''
        if len(line) > 0:
            password = line[0]
        elif 'KEYSTORE_PASSWORD' in os.environ:
            password = os.environ['KEYSTORE_PASSWORD']
        else:
            self.print_line("No password provided")
            return
        cmd = './gradlew :app:install%sRelease -PKEYSTORE_PASSWORD=%s' % (FLAVOUR.title(), password)
        log("running " + './gradlew :app:install%sRelease -PKEYSTORE_PASSWORD=..' % (FLAVOUR.title()))
        os.system(cmd)
        self.do_run()

    def do_install_apk(self,line):
        """Installs the RELEASE_APK"""
        self.install_apk(RELEASE_APK)


    def do_install_debug(self, line):
        """Installs the latest debug apk"""
        cmd = './gradlew :app:install%sDebug ' % (FLAVOUR.title())
        os.system(cmd)
        self.do_run()

        """cmd = './gradlew :app:assemble%sDebug ' % (FLAVOUR.title())
        os.system(cmd)
        self.do_uninstall()
        os.system('adb install %s' % APK_DEBUG)
        self.do_run()
        """

    def install_apk(self, apk):
        log("installing %s" % apk)
        os.system('adb install -r %s' % apk)
        self.do_run()

    def do_run(self, line=''):
        log("starting %s" % PACKAGE)
        os.system("adb shell monkey -p %s -c android.intent.category.LAUNCHER 1" % PACKAGE)

    def do_uninstall(self, line=''):
        log("removing any existing version")
        os.system('adb uninstall ' + PACKAGE)

    def do_backup(self, line):
        """Creates a backup of the currently installed app in a BACKUP_DIR/VERSION_CODE directory"""
        s = subprocess.check_output(('adb shell dumpsys package %s' % PACKAGE).split())
        log("data is %s"% s)
        versionCode = [int(a[12:]) for a in s.split() if a.startswith(b'versionCode=')][0]

        if not os.path.exists(BACKUPS_DIR):
            os.mkdir(BACKUPS_DIR)
        backup_dir = os.path.join(BACKUPS_DIR, str(versionCode))
        if os.path.exists(backup_dir):
            log("%s already exists" % backup_dir)
            return

        log("creating backup at %s" % backup_dir)
        os.mkdir(backup_dir)
        os.chdir(backup_dir)
        try:
            s = subprocess.check_output(('adb shell pm path ' + PACKAGE).split())
            if len(s) == 0:
                log("%s is not installed" % PACKAGE)
                return False
            s = s.decode()
            s = s[s.find(':') + 1:].strip()
            os.system('adb pull %s app.apk' % s)
            for dir in 'databases', 'shared_prefs':
                os.system('adb pull /data/data/%s/%s' % (PACKAGE, dir))
        finally:
            os.chdir(HOME_DIR)

    def do_list(self, line=''):
        """list available backups"""
        log("available backups: %s" % ','.join(os.listdir(BACKUPS_DIR)))

    def do_restore(self, line):
        log("do_restore: line: %s" % line)
        self.do_uninstall()
        line = line.split()
        backup = ''
        if len(line) == 0:
            if os.path.exists(BACKUPS_DIR):
                backups = os.listdir(BACKUPS_DIR)
                if len(backups) == 1:
                    backup = backups[0]
                else:
                    log("usage: restore version_code")
                    self.do_list()
                    return
        else:
            backup = line[0]

        backup_dir = os.path.join(BACKUPS_DIR, backup)
        if not os.path.exists(backup_dir):
            log("%s doesnt exist" % backup_dir)
            self.do_list()
            return

        os.chdir(backup_dir)

        log("installing base.apk")
        os.system("adb install base.apk")

        s = subprocess.check_output(('adb shell dumpsys package %s' % PACKAGE).split())
        uid = [int(a[7:]) for a in s.split() if a.startswith(b'userId=')][0]
        log("uid: %d" % uid)
        perms = "%d:%d" % (uid, uid)

        for d in "databases", "shared_prefs":
            os.system("adb push %s/ /data/data/%s" % (d, PACKAGE))
            os.system('adb shell chown -R %s "/data/data/%s/%s"' % (perms, PACKAGE, d))

        self.do_run()

    def emptyline(self):
        return None

    def print_line(self, line):
        cprint(line, 'cyan', attrs=[])
        # self.print_prompt()

    def do_EOF(self, line):
        return True


if __name__ == '__main__':
    main = Main()
    main.onecmd("help")
    if len(sys.argv) > 1:
        main.onecmd(' '.join(sys.argv[1:]))
    else:
        main.cmdloop()
