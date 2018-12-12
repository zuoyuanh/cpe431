echo "Testing ARM assembly code..."
echo "============================================="

cat test_cases_names.txt | while read filename
do
   echo -n "case $filename... "

   cd ../tests/test_m2

   if [[ $(`gcc -c $filename.s`) ]]; then
       echo -n "error while compiling .s code"
   else
      if [[ $(gcc $filename.o -o $filename.out) ]]; then
         echo "error while linking"
      else
         if [[ $(./$filename.out < ../../benchmarks/$filename/input > $filename.myout) ]]; then
            echo "runtime error"
         else
            if [[ $(diff "../../benchmarks/$filename/output" $filename.myout) ]]; then
               echo "diff shows difference"
            else
               echo "passed"
            fi
         fi
      fi
   fi

   cd ../..

   cd tester
done

echo "Testing ARM assembly code with longer inputs..."

cat test_cases_names.txt | while read filename
do
   echo -n "case $filename... "

   cd ../tests/test_m2

   if [[ $(./$filename.out < ../../benchmarks/$filename/input.longer > $filename.myout.longer) ]]; then
      echo "runtime error"
   else
      if [[ $(diff "../../benchmarks/$filename/output.longer" $filename.myout.longer) ]]; then
         echo "diff shows difference"
      else
         echo -n "passed"
      fi
   fi

   cd ../..

   cd tester
done
