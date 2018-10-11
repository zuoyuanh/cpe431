target triple="i686"
%struct.simple = type {i32}
%struct.foo = type {i32, i32, %struct.simple*}

@globalfoo = common global %struct.foo* null, align 8

define void @tailrecursive(i32 %num)
{
LU1: 
	%_P_num = alloca i32
	store i32 %num, i32* %_P_num
	%unused = alloca %struct.foo*
	%u0 = load i32* %_P_num
	%u1 = icmp sle i32 %u0, 0
	br i1 %u1, label %LU2, label %LU3
LU2: 
	br label %LU0
LU3: 
	br label %LU4
LU4: 
	%u2 = call i8* @malloc(i32 24)
	%u3 = bitcast i8* %u2 to %struct.foo*
	store %struct.foo* %u3, %struct.foo** %unused
	%u4 = load i32* %_P_num
	%u5 = sub i32 %u4, 1
	call void @tailrecursive(i32 %u5)
	br label %LU0
LU0: 
	ret void
}

define i32 @add(i32 %x, i32 %y)
{
LU6: 
	%_retval_ = alloca i32
	%_P_x = alloca i32
	store i32 %x, i32* %_P_x
	%_P_y = alloca i32
	store i32 %y, i32* %_P_y
	%u7 = load i32* %_P_x
	%u8 = load i32* %_P_y
	%u9 = add i32 %u7, %u8
	store i32 %u9, i32* %_retval_
	br label %LU5
LU5: 
	%u6 = load i32* %_retval_
	ret i32 %u6
}

define void @domath(i32 %num)
{
LU8: 
	%_P_num = alloca i32
	store i32 %num, i32* %_P_num
	%math1 = alloca %struct.foo*
	%math2 = alloca %struct.foo*
	%tmp = alloca i32
	%u10 = call i8* @malloc(i32 24)
	%u11 = bitcast i8* %u10 to %struct.foo*
	store %struct.foo* %u11, %struct.foo** %math1
	%u12 = load %struct.foo** %math1
	%u13 = getelementptr %struct.foo* %u12, i1 0, i32 2
	%u14 = call i8* @malloc(i32 8)
	%u15 = bitcast i8* %u14 to %struct.simple*
	store %struct.simple* %u15, %struct.simple** %u13
	%u16 = call i8* @malloc(i32 24)
	%u17 = bitcast i8* %u16 to %struct.foo*
	store %struct.foo* %u17, %struct.foo** %math2
	%u18 = load %struct.foo** %math2
	%u19 = getelementptr %struct.foo* %u18, i1 0, i32 2
	%u20 = call i8* @malloc(i32 8)
	%u21 = bitcast i8* %u20 to %struct.simple*
	store %struct.simple* %u21, %struct.simple** %u19
	%u22 = load %struct.foo** %math1
	%u23 = getelementptr %struct.foo* %u22, i1 0, i32 0
	%u24 = load i32* %_P_num
	store i32 %u24, i32* %u23
	%u25 = load %struct.foo** %math2
	%u26 = getelementptr %struct.foo* %u25, i1 0, i32 0
	store i32 3, i32* %u26
	%u27 = load %struct.foo** %math1
	%u28 = getelementptr %struct.foo* %u27, i1 0, i32 2
	%u29 = load %struct.simple** %u28
	%u30 = getelementptr %struct.simple* %u29, i1 0, i32 0
	%u31 = load %struct.foo** %math1
	%u32 = getelementptr %struct.foo* %u31, i1 0, i32 0
	%u33 = load i32* %u32
	store i32 %u33, i32* %u30
	%u34 = load %struct.foo** %math2
	%u35 = getelementptr %struct.foo* %u34, i1 0, i32 2
	%u36 = load %struct.simple** %u35
	%u37 = getelementptr %struct.simple* %u36, i1 0, i32 0
	%u38 = load %struct.foo** %math2
	%u39 = getelementptr %struct.foo* %u38, i1 0, i32 0
	%u40 = load i32* %u39
	store i32 %u40, i32* %u37
	%u41 = load i32* %_P_num
	%u42 = icmp sgt i32 %u41, 0
	br i1 %u42, label %LU9, label %LU10
LU9: 
	%u43 = load %struct.foo** %math1
	%u44 = getelementptr %struct.foo* %u43, i1 0, i32 0
	%u45 = load i32* %u44
	%u46 = load %struct.foo** %math2
	%u47 = getelementptr %struct.foo* %u46, i1 0, i32 0
	%u48 = load i32* %u47
	%u49 = mul i32 %u45, %u48
	store i32 %u49, i32* %tmp
	%u50 = load i32* %tmp
	%u51 = load %struct.foo** %math1
	%u52 = getelementptr %struct.foo* %u51, i1 0, i32 2
	%u53 = load %struct.simple** %u52
	%u54 = getelementptr %struct.simple* %u53, i1 0, i32 0
	%u55 = load i32* %u54
	%u56 = mul i32 %u50, %u55
	%u57 = load %struct.foo** %math2
	%u58 = getelementptr %struct.foo* %u57, i1 0, i32 0
	%u59 = load i32* %u58
	%u60 = sdiv i32 %u56, %u59
	store i32 %u60, i32* %tmp
	%u61 = load %struct.foo** %math2
	%u62 = getelementptr %struct.foo* %u61, i1 0, i32 2
	%u63 = load %struct.simple** %u62
	%u64 = getelementptr %struct.simple* %u63, i1 0, i32 0
	%u65 = load i32* %u64
	%u66 = load %struct.foo** %math1
	%u67 = getelementptr %struct.foo* %u66, i1 0, i32 0
	%u68 = load i32* %u67
	%u69 = call i32@add (i32 %u65, i32 %u68)
	store i32 %u69, i32* %tmp
	%u70 = load %struct.foo** %math2
	%u71 = getelementptr %struct.foo* %u70, i1 0, i32 0
	%u72 = load i32* %u71
	%u73 = load %struct.foo** %math1
	%u74 = getelementptr %struct.foo* %u73, i1 0, i32 0
	%u75 = load i32* %u74
	%u76 = sub i32 %u72, %u75
	store i32 %u76, i32* %tmp
	%u77 = load i32* %_P_num
	%u78 = sub i32 %u77, 1
	store i32 %u78, i32* %_P_num
	%u79 = load i32* %_P_num
	%u80 = icmp sgt i32 %u79, 0
	br i1 %u80, label %LU9, label %LU10
LU10: 
	%u81 = load %struct.foo** %math1
	%u82 = bitcast %struct.foo* %u81 to i8*
	call void @free(i8* %u82)
	%u83 = load %struct.foo** %math2
	%u84 = bitcast %struct.foo* %u83 to i8*
	call void @free(i8* %u84)
	br label %LU7
LU7: 
	ret void
}

define void @objinstantiation(i32 %num)
{
LU12: 
	%_P_num = alloca i32
	store i32 %num, i32* %_P_num
	%tmp = alloca %struct.foo*
	%u85 = load i32* %_P_num
	%u86 = icmp sgt i32 %u85, 0
	br i1 %u86, label %LU13, label %LU14
LU13: 
	%u87 = call i8* @malloc(i32 24)
	%u88 = bitcast i8* %u87 to %struct.foo*
	store %struct.foo* %u88, %struct.foo** %tmp
	%u89 = load %struct.foo** %tmp
	%u90 = bitcast %struct.foo* %u89 to i8*
	call void @free(i8* %u90)
	%u91 = load i32* %_P_num
	%u92 = sub i32 %u91, 1
	store i32 %u92, i32* %_P_num
	%u93 = load i32* %_P_num
	%u94 = icmp sgt i32 %u93, 0
	br i1 %u94, label %LU13, label %LU14
LU14: 
	br label %LU11
LU11: 
	ret void
}

define i32 @ackermann(i32 %m, i32 %n)
{
LU16: 
	%_retval_ = alloca i32
	%_P_m = alloca i32
	store i32 %m, i32* %_P_m
	%_P_n = alloca i32
	store i32 %n, i32* %_P_n
	%u96 = load i32* %_P_m
	%u97 = icmp eq i32 %u96, 0
	br i1 %u97, label %LU17, label %LU18
LU17: 
	%u98 = load i32* %_P_n
	%u99 = add i32 %u98, 1
	store i32 %u99, i32* %_retval_
	br label %LU15
LU18: 
	br label %LU19
LU19: 
	%u100 = load i32* %_P_n
	%u101 = icmp eq i32 %u100, 0
	br i1 %u101, label %LU20, label %LU21
LU20: 
	%u102 = load i32* %_P_m
	%u103 = sub i32 %u102, 1
	%u104 = call i32@ackermann (i32 %u103, i32 1)
	store i32 %u104, i32* %_retval_
	br label %LU15
LU21: 
	%u105 = load i32* %_P_m
	%u106 = sub i32 %u105, 1
	%u107 = load i32* %_P_m
	%u108 = load i32* %_P_n
	%u109 = sub i32 %u108, 1
	%u110 = call i32@ackermann (i32 %u107, i32 %u109)
	%u111 = call i32@ackermann (i32 %u106, i32 %u110)
	store i32 %u111, i32* %_retval_
	br label %LU15
LU22: 
	br label %LU15
LU15: 
	%u95 = load i32* %_retval_
	ret i32 %u95
}

define i32 @main()
{
LU24: 
	%_retval_ = alloca i32
	%a = alloca i32
	%b = alloca i32
	%c = alloca i32
	%d = alloca i32
	%e = alloca i32
	call i32 (i8*, ...)* @scanf(i8* getelementptr inbounds ([4 x i8]* @.read, i32 0, i32 0), i32* %a)
	call i32 (i8*, ...)* @scanf(i8* getelementptr inbounds ([4 x i8]* @.read, i32 0, i32 0), i32* %b)
	call i32 (i8*, ...)* @scanf(i8* getelementptr inbounds ([4 x i8]* @.read, i32 0, i32 0), i32* %c)
	call i32 (i8*, ...)* @scanf(i8* getelementptr inbounds ([4 x i8]* @.read, i32 0, i32 0), i32* %d)
	call i32 (i8*, ...)* @scanf(i8* getelementptr inbounds ([4 x i8]* @.read, i32 0, i32 0), i32* %e)
	%u113 = load i32* %a
	call void @tailrecursive(i32 %u113)
	%u114 = load i32* %a
	call i32 (i8*, ...)* @printf(i8* getelementptr inbounds ([5 x i8]* @.println, i32 0, i32 0), i32 %u114)
	%u115 = load i32* %b
	call void @domath(i32 %u115)
	%u116 = load i32* %b
	call i32 (i8*, ...)* @printf(i8* getelementptr inbounds ([5 x i8]* @.println, i32 0, i32 0), i32 %u116)
	%u117 = load i32* %c
	call void @objinstantiation(i32 %u117)
	%u118 = load i32* %c
	call i32 (i8*, ...)* @printf(i8* getelementptr inbounds ([5 x i8]* @.println, i32 0, i32 0), i32 %u118)
	%u119 = load i32* %d
	%u120 = load i32* %e
	%u121 = call i32@ackermann (i32 %u119, i32 %u120)
	call i32 (i8*, ...)* @printf(i8* getelementptr inbounds ([5 x i8]* @.println, i32 0, i32 0), i32 %u121)
	br label %LU23
LU23: 
	%u112 = load i32* %_retval_
	ret i32 %u112
}

declare i8* @malloc(i32)
declare void @free(i8*)
declare i32 @printf(i8*, ...)
declare i32 @scanf(i8*, ...)
@.println = private unnamed_addr constant [5 x i8] c"%ld\0A\00", align 1
@.print = private unnamed_addr constant [5 x i8] c"%ld \00", align 1
@.read = private unnamed_addr constant [4 x i8] c"%ld\00", align 1
@.read_scratch = common global i32 0, align 8
