LINUX

Carnivore has been tested on Ubuntu (64bit). Note that you must launch
Processing as root:

	sudo ./processing

Running as root is necessary for Carnivore to put your network adaptors
in promiscuous mode. But be warned that running applications as root is
risker and can introduce security problems. Please proceed with caution.

We're trying to test on other flavors of Linux as much as we can. If the
jpcap library (libjpcap-linux64bit.so) isn't working for you, here are
instructions on how to build it from source:

1) Download jpcap-0.01.16.tar.gz from
http://sourceforge.net/projects/jpcap/files/jpcap/v0.01.16/

2) change the variables in scripts/env_jpcap per instructions in
docs/BUILD.unix

3) run scripts/env_jpcap

4) edit lines 66 and 67 of
jpcap-0.01.16/src/java/net/sourceforge/jpcap/capture/makefile so they
look like this:

		-I/usr/lib/jvm/java-7-openjdk-amd64/include
	LIBS = -lnsl /usr/lib/x86_64-linux-gnu/libpcap.so

You might need to use different paths if your system has a different
configuration. For the first line, you want the directory where jni.h
lives. For the second line you want the path to libpcap.so. On your
system it might just be inside /usr/lib.

5) edit line 678 of
jpcap-0.01.16/src/java/net/sourceforge/jpcap/capture/jpcap.c and comment
out "(char*)" like this:

	for(;ifr < last; /*(char*)*/ifr += ifrSize) {

6) cd back to the top level jpcap-0.01.16 directory and run `make` (you
might need to run `make clean` first). This will create libjpcap.so
inside jpcap-0.01.16/src/java/net/sourceforge/jpcap/capture.

7) Rename libjpcap.so to libjpcap-linux64bit.so and copy it over to the
carnivore/library folder in your Processing library folder (ex:
Processing/libraries/carnivore/library/libjpcap-linux64bit.so)

