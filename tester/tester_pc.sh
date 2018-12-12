cd ..
touch *

echo "Compiling Java Program..."

make > /dev/null

cd tests/test_m2
rm *.s
rm *.o
rm *.out
rm *.myout

echo
echo "Generating ARM assembly code..."
echo "============================================="

cd ../..
cd tester

cat test_cases_names.txt | while read filename
do
   echo -n "generating $filename.s... "

   cd ..
   java MiniCompiler -llvm "tests/test_m2/$filename.mini" > /dev/null

   echo "done"

   cd tester
done