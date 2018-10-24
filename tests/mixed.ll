target triple="i686"
%struct.simple = type {i32}
%struct.foo = type {i32, i32, %struct.simple*}

@globalfoo = common global %struct.foo* null, align 8

define void @tailrecursive()
{
LU1: 
	%u1 = icmp sle i32 %num, 0
	br i1 %u1, label %LU2, label %LU3
LU2: 
	br label %LU0
LU3: 
	br label %LU4
LU4: 
	%u2 = call i8* @malloc(i32 24)
	%u3 = bitcast i8* %u2 to %struct.foo*
	%u5 = sub i32 %num, 1
	call void @tailrecursive(i32 %u5)
	br label %LU0
LU0: 
	ret void
}

define i32 @add()
{
LU6: 
	%u9 = add i32 %x, %y
	store i32 %u9, i32* %_retval_
	br label %LU5
LU5: 
	%u6 = load i32* %_retval_
	ret i32 %u6
}

define void @domath()
{
bar: IDENTIFIER NOT FOUND 2
bar: IDENTIFIER NOT FOUND 2
simp: IDENTIFIER NOT FOUND 2
bar: IDENTIFIER NOT FOUND 2
simp: IDENTIFIER NOT FOUND 2
bar: IDENTIFIER NOT FOUND 2
bar: IDENTIFIER NOT FOUND 2
bar: IDENTIFIER NOT FOUND 2
LU8: 
	%u10 = call i8* @malloc(i32 24)
	%u11 = bitcast i8* %u10 to %struct.foo*
	%u13 = getelementptr %struct.foo* %u11, i1 0, i32 2
	%u14 = call i8* @malloc(i32 8)
	%u15 = bitcast i8* %u14 to %struct.simple*
	store %struct.simple* %u15, %struct.simple** %u13
	%u16 = call i8* @malloc(i32 24)
	%u17 = bitcast i8* %u16 to %struct.foo*
	%u19 = getelementptr %struct.foo* %u17, i1 0, i32 2
	%u20 = call i8* @malloc(i32 8)
	%u21 = bitcast i8* %u20 to %struct.simple*
	store %struct.simple* %u21, %struct.simple** %u19
	%u23 = getelementptr %struct.foo* %u11, i1 0, i32 0
	store i32 %num, i32* %u23
	%u26 = getelementptr %struct.foo* %u17, i1 0, i32 0
	store i32 3, i32* %u26
	%u29 = load %struct.simple** %u28
	%u30 = getelementptr %struct.simple* %u29, i1 0, i32 0
	%u33 = load i32* %u32
	store i32 %u33, i32* %u30
	%u36 = load %struct.simple** %u35
	%u37 = getelementptr %struct.simple* %u36, i1 0, i32 0
	%u40 = load i32* %u39
	store i32 %u40, i32* %u37
	%u42 = icmp sgt i32 %num, 0
	br i1 %u42, label %LU9, label %LU10
LU9: 
	%u44 = phi i32 (math1)
	%u46 = phi i32 (math2)
	%u60 = phi i32 (num)
	%u47 = mul i32 unknown, unknown
	%u50 = mul i32 %u47, unknown
	%u52 = sdiv i32 %u50, unknown
	%u55 = call i32 @add ()
	%u58 = sub i32 unknown, unknown
	%u61 = sub i32 %u60, 1
	%u63 = icmp sgt i32 %u61, 0
	br i1 %u63, label %LU9, label %LU10
LU10: 
	%u65 = phi i32 [%u11, %LU10], [%u44, %LU10](math1)
	%u68 = phi i32 [%u17, %LU10], [%u46, %LU10](math2)
	%u66 = bitcast i32 %u65 to i8*
	call void @free(i8* %u66)
	%u69 = bitcast i32 %u68 to i8*
	call void @free(i8* %u69)
	br label %LU7
LU7: 
	ret void
}

define void @objinstantiation()
{
LU12: 
	%u71 = icmp sgt i32 %num, 0
	br i1 %u71, label %LU13, label %LU14
LU13: 
	%u77 = phi i32 (num)
	%u72 = call i8* @malloc(i32 24)
	%u73 = bitcast i8* %u72 to %struct.foo*
	%u75 = bitcast %struct.foo* %u73 to i8*
	call void @free(i8* %u75)
	%u78 = sub i32 %u77, 1
	%u80 = icmp sgt i32 %u78, 0
	br i1 %u80, label %LU13, label %LU14
LU14: 
	br label %LU11
LU11: 
	ret void
}

define i32 @ackermann()
{
LU16: 
	%u83 = icmp eq i32 %m, 0
	br i1 %u83, label %LU17, label %LU18
LU17: 
	%u85 = add i32 %n, 1
	store i32 %u85, i32* %_retval_
	br label %LU15
LU18: 
	br label %LU19
LU19: 
	%u87 = icmp eq i32 %n, 0
	br i1 %u87, label %LU20, label %LU21
LU20: 
	%u89 = sub i32 %m, 1
	%u90 = call i32 @ackermann (i32 %u89, i32 1)
	store i32 %u90, i32* %_retval_
	br label %LU15
LU21: 
	%u92 = sub i32 %m, 1
	%u95 = sub i32 %n, 1
	%u96 = call i32 @ackermann (i32 %%m, i32 %u95)
	%u97 = call i32 @ackermann (i32 %u92, i32 %u96)
	store i32 %u97, i32* %_retval_
	br label %LU15
LU22: 
	br label %LU15
LU15: 
	%u81 = load i32* %_retval_
	ret i32 %u81
}

define i32 @main()
{
LU24: 
	call i32 (i8*, ...)* @scanf(i8* getelementptr inbounds ([4 x i8]* @.read, i32 0, i32 0), i32* @.read_scratch)
u99 = load i32* @.read_scratch
	call i32 (i8*, ...)* @scanf(i8* getelementptr inbounds ([4 x i8]* @.read, i32 0, i32 0), i32* @.read_scratch)
u100 = load i32* @.read_scratch
	call i32 (i8*, ...)* @scanf(i8* getelementptr inbounds ([4 x i8]* @.read, i32 0, i32 0), i32* @.read_scratch)
u101 = load i32* @.read_scratch
	call i32 (i8*, ...)* @scanf(i8* getelementptr inbounds ([4 x i8]* @.read, i32 0, i32 0), i32* @.read_scratch)
u102 = load i32* @.read_scratch
	call i32 (i8*, ...)* @scanf(i8* getelementptr inbounds ([4 x i8]* @.read, i32 0, i32 0), i32* @.read_scratch)
u103 = load i32* @.read_scratch
	call void @tailrecursive(i32 %u99)
	call i32 (i8*, ...)* @printf(i8* getelementptr inbounds ([5 x i8]* @.println, i32 0, i32 0), i32 %u99)
	call void @domath(i32 %u100)
	call i32 (i8*, ...)* @printf(i8* getelementptr inbounds ([5 x i8]* @.println, i32 0, i32 0), i32 %u100)
	call void @objinstantiation(i32 %u101)
	call i32 (i8*, ...)* @printf(i8* getelementptr inbounds ([5 x i8]* @.println, i32 0, i32 0), i32 %u101)
	%u112 = call i32 @ackermann (i32 %u102, i32 %u103)
	call i32 (i8*, ...)* @printf(i8* getelementptr inbounds ([5 x i8]* @.println, i32 0, i32 0), i32 %u112)
	store i32 0, i32* %_retval_
	br label %LU23
LU23: 
	%u98 = load i32* %_retval_
	ret i32 %u98
}

declare i8* @malloc(i32)
declare void @free(i8*)
declare i32 @printf(i8*, ...)
declare i32 @scanf(i8*, ...)
@.println = private unnamed_addr constant [5 x i8] c"%ld\0A\00", align 1
@.print = private unnamed_addr constant [5 x i8] c"%ld \00", align 1
@.read = private unnamed_addr constant [4 x i8] c"%ld\00", align 1
@.read_scratch = common global i32 0, align 8
