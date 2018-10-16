FILE=$1

java MiniCompiler -stack "tests/test_m2/$FILE.mini" &> /dev/null
cd tests/test_m2
clang "$FILE.ll" -m32
./a.out < "../../benchmarks/$FILE/input" > $FILE.myout
diff "../../benchmarks/$FILE/output" $FILE.myout

