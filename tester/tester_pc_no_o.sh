cd ..
touch *

echo "Compiling Java Program..."

make > /dev/null

cd tests/test_m2
rm *.s &> null
rm *.o &> null
rm *.out &> null
rm *.myout &> null
rm *.myout.longer &> null

echo
echo "Generating ARM assembly code..."
echo "============================================="

cd ../..
cd tester

cat test_cases_names.txt | while read filename
do
   echo -n "generating $filename.ll $filename.s... "

   cd ..
   java MiniCompiler -llvm -no-o "tests/test_m2/$filename.mini" > /dev/null

   echo "done"

   cd tester
done
