to compile -
javac --release 8 -cp ".;lib/flatlaf-3.6.jar" -d bin java/main/Main.java java/model/*.java java/controller/*.java java/view/*.java java/service/*.java java/database/*.java util/IconFactory.java

to run -
for windows -
java -cp "bin;lib/flatlaf-3.6.jar" main.Main

for linux/max -
java -cp "bin:lib/flatlaf-3.6.jar" main.Main
