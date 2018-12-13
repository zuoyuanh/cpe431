echo "Timer"
echo "============================================="

cat test_cases_names.txt | while read filename
do
   cd benchmarks/$filename

   echo "[Test case $filename]"
   
   gcc -O0 $filename.c -o $filename.gcc.no_o.out
   gcc -O3 $filename.c -o $filename.gcc.out
   gcc -c $filename.s
   gcc -c $filename.no_o.s
   gcc $filename.o -o $filename.out
   gcc $filename.no_o.s -o $filename.no_o.out

   echo "time for optimized ARM: " 
   time $(./$filename.out < ../../benchmarks/$filename/input > $filename.myout) 
   echo "--------------"
   echo "time for non-optimized ARM: " 
   time $(./$filename.no_o.out < ../../benchmarks/$filename/input > $filename.myout) 
   echo "--------------"
   echo "time for optimized GCC: " 
   time $(./$filename.gcc.out < ../../benchmarks/$filename/input > $filename.myout) 
   echo "--------------"
   echo "time for non-optimized GCC: " 
   time $(./$filename.gcc.no_o.out < ../../benchmarks/$filename/input > $filename.myout) 
   echo "--------------"

   echo
   echo
   echo

   cd ../..

done
