to compile -
javac --release 8 -cp "lib/flatlaf-3.6.jar" -d bin java/main/Main.java java/model/_.java java/view/_.java java/controller/_.java java/service/_.java java/database/\*.java

to run -
for windows -
java -cp "bin;lib/flatlaf-3.6.jar" main.Main

for linux/max -
java -cp "bin:lib/flatlaf-3.6.jar" main.Main
