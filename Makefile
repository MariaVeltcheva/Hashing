#implemented as per code in the slides

SRCDIR = src
BINDIR = bin
DOCDIR = doc

vpath %.java $(SRCDIR)

JFLAGS = -g -classpath $(BINDIR) -d $(BINDIR)
JC = javac

#defines rule for building .class files from .java files
.SUFFIXES: .java .class

.java.class:
	$(JC) $(JFLAGS) $(SRCDIR)/$*.java
	#$(JC) $(JFLAGS) $*.java

#classes that will be built
CLASSES = \
	Shuffled.java \
	Hashing.java

#default rule - invoked when running make in the terminal
default: classes

#classes rule - invoked by default rule
classes: $(CLASSES:.java=.class)

#doc rule - generates javadocs when make doc is run in the terminal
docs:
	javadoc -d $(DOCDIR) $(SRCDIR)/*.java

#rule for deleting classfiles and javadocs - invoked when running make clean in the terminal
clean:
	$(RM) $(BINDIR)/*.class
	$(RM) $(DOCDIR)/*
