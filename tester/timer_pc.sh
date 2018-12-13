echo "Timer"
echo "============================================="

cat test_cases_names.txt | while read filename
do
   echo -n "generating $filename.ll $filename.s... "

   cd benchmarks/$filename
   rm *.s &> /dev/null
   rm *.ll &> /dev/null
   rm *.o &> /dev/null

   cd ../../..

   java MiniCompiler -llvm -no-o tester/benchmarks/$filename/$filename.mini > /dev/null
   mv tester/benchmarks/$filename/$filename.ll tester/benchmarks/$filename/$filename.no_o.ll
   mv tester/benchmarks/$filename/$filename.s tester/benchmarks/$filename/$filename.no_o.s

   java MiniCompiler -llvm tester/benchmarks/$filename/$filename.mini > /dev/null

   cd tester

done
