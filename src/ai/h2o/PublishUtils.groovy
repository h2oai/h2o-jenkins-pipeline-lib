#!/usr/bin/groovy
@GrabResolver(name="gradle", root="http://repo.gradle.org/gradle/libs-releases-local")
@Grab("org.gradle:gradle-tooling-api:4.0")
@Grab("org.slf4j:slf4j-simple:1.6.4")

def buildProjectFileContent = """
plugins {
  id 'ivy-publish'
}
"""

def withClosable = { closable, closure ->
    try {
        closure(closable)
    } finally {
        closable.close()
    }
}

def writeFile(File destination, String content) throws IOException {
	BufferedWriter output = null;
	try {
		output = new BufferedWriter(new FileWriter(destination));
		output.write(content);
	} finally {
		if (output != null) {
			output.close();
		}
	}
}

