target triple="i686"
%struct.simple = type {i32}
%struct.foo = type {i32, i32, %struct.simple*}

@globalfoo = common global %struct.foo* null, align 8

define void @tailrecursive(i32 %num)
{
LU1: 
	%u0 = icmp sle i32 %num, 0
	br i1 %u0, label %LU2, label %LU3
LU2: 
	br label %LU0
LU3: 
	br label %LU4
LU4: 
	%u3 = sub i32 %num, 1
	call void @tailrecursive(i32 %u3)
	br label %LU0
LU0: 
	ret void
}

define i32 @add(i32 %x, i32 %y)
{
LU6: 
	%u4 = add i32 %x, %y
		br label %LU5
LU5: 
	%u5 = phi i32 [%u4, %LU6]
	ret i32 %u5
}

define void @domath(i32 %num)
{
LU8: 
	%u6 = call i8* @malloc(i32 24)
	%u7 = bitcast i8* %u6 to %struct.foo*
	%u8 = getelementptr %struct.foo* %u7, i1 0, i32 2
	%u9 = call i8* @malloc(i32 8)
	%u10 = bitcast i8* %u9 to %struct.simple*
	store %struct.simple* %u10, %struct.simple** %u8
	%u11 = call i8* @malloc(i32 24)
	%u12 = bitcast i8* %u11 to %struct.foo*
	%u13 = getelementptr %struct.foo* %u12, i1 0, i32 2
	%u14 = call i8* @malloc(i32 8)
	%u15 = bitcast i8* %u14 to %struct.simple*
	store %struct.simple* %u15, %struct.simple** %u13
	%u16 = getelementptr %struct.foo* %u7, i1 0, i32 0
	store i32 %num, i32* %u16
	%u17 = getelementptr %struct.foo* %u12, i1 0, i32 0
	store i32 3, i32* %u17
	%u18 = getelementptr %struct.foo* %u7, i1 0, i32 2
	%u19 = load %struct.simple** %u18
	%u20 = getelementptr %struct.simple* %u19, i1 0, i32 0
	%u21 = getelementptr %struct.foo* %u7, i1 0, i32 0
	%u22 = load i32* %u21
	store i32 %u22, i32* %u20
	%u23 = getelementptr %struct.foo* %u12, i1 0, i32 2
	%u24 = load %struct.simple** %u23
	%u25 = getelementptr %struct.simple* %u24, i1 0, i32 0
	%u26 = getelementptr %struct.foo* %u12, i1 0, i32 0
	%u27 = load i32* %u26
	store i32 %u27, i32* %u25
	%u28 = icmp sgt i32 %num, 0
	br i1 %u28, label %LU9, label %LU10
LU9: 
	%u56 = phi i32 [%num, %LU8], [%u57, %LU9]
	%u32 = phi %struct.foo* [%u12, %LU8], [%u32, %LU9]
	%u29 = phi %struct.foo* [%u7, %LU8], [%u29, %LU9]
	%u44 = getelementptr %struct.foo* %u32, i1 0, i32 2
	%u45 = load %struct.simple** %u44
	%u46 = getelementptr %struct.simple* %u45, i1 0, i32 0
	%u47 = load i32* %u46
	%u48 = getelementptr %struct.foo* %u29, i1 0, i32 0
	%u49 = load i32* %u48
	%u50 = call i32 @add(i32 %u47, i32 %u49)
	%u57 = sub i32 %u56, 1
	%u58 = icmp sgt i32 %u57, 0
	br i1 %u58, label %LU9, label %LU10
LU10: 
	br label %LU7
LU7: 
	ret void
}

define void @objinstantiation(i32 %num)
{
LU12: 
	%u61 = icmp sgt i32 %num, 0
	br i1 %u61, label %LU13, label %LU14
LU13: 
	%u64 = phi i32 [%num, %LU12], [%u65, %LU13]
	%u65 = sub i32 %u64, 1
	%u66 = icmp sgt i32 %u65, 0
	br i1 %u66, label %LU13, label %LU14
LU14: 
	br label %LU11
LU11: 
	ret void
}

define i32 @ackermann(i32 %m, i32 %n)
{
LU16: 
	%u67 = icmp eq i32 %m, 0
	br i1 %u67, label %LU17, label %LU18
LU17: 
	%u68 = add i32 %n, 1
		br label %LU15
LU18: 
	br label %LU19
LU19: 
	%u69 = icmp eq i32 %n, 0
	br i1 %u69, label %LU20, label %LU21
LU20: 
	%u70 = sub i32 %m, 1
	%u71 = call i32 @ackermann(i32 %u70, i32 1)
		br label %LU15
LU21: 
	%u72 = sub i32 %m, 1
	%u73 = sub i32 %n, 1
	%u74 = call i32 @ackermann(i32 %m, i32 %u73)
	%u75 = call i32 @ackermann(i32 %u72, i32 %u74)
		br label %LU15
LU15: 
	%u76 = phi i32 [%u68, %LU17], [%u71, %LU20], [%u75, %LU21]
	ret i32 %u76
}

define i32 @main()
{
LU24: 
	call i32 (i8*, ...)* @scanf(i8* getelementptr inbounds ([4 x i8]* @.read, i32 0, i32 0), i32* @.read_scratch)
	%u77 = load i32* @.read_scratch
	call i32 (i8*, ...)* @scanf(i8* getelementptr inbounds ([4 x i8]* @.read, i32 0, i32 0), i32* @.read_scratch)
	%u78 = load i32* @.read_scratch
	call i32 (i8*, ...)* @scanf(i8* getelementptr inbounds ([4 x i8]* @.read, i32 0, i32 0), i32* @.read_scratch)
	%u79 = load i32* @.read_scratch
	call i32 (i8*, ...)* @scanf(i8* getelementptr inbounds ([4 x i8]* @.read, i32 0, i32 0), i32* @.read_scratch)
	%u80 = load i32* @.read_scratch
	call i32 (i8*, ...)* @scanf(i8* getelementptr inbounds ([4 x i8]* @.read, i32 0, i32 0), i32* @.read_scratch)
	%u81 = load i32* @.read_scratch
	call void @tailrecursive(i32 %u77)
	call i32 (i8*, ...)* @printf(i8* getelementptr inbounds ([5 x i8]* @.println, i32 0, i32 0), i32 %u77)
	call void @domath(i32 %u78)
	call i32 (i8*, ...)* @printf(i8* getelementptr inbounds ([5 x i8]* @.println, i32 0, i32 0), i32 %u78)
	call void @objinstantiation(i32 %u79)
	call i32 (i8*, ...)* @printf(i8* getelementptr inbounds ([5 x i8]* @.println, i32 0, i32 0), i32 %u79)
	%u82 = call i32 @ackermann(i32 %u80, i32 %u81)
	call i32 (i8*, ...)* @printf(i8* getelementptr inbounds ([5 x i8]* @.println, i32 0, i32 0), i32 %u82)
	br label %LU23
LU23: 
	%u83 = phi i32 [0, %LU24]
	ret i32 %u83
}

declare i8* @malloc(i32)
declare void @free(i8*)
declare i32 @printf(i8*, ...)
declare i32 @scanf(i8*, ...)
@.println = private unnamed_addr constant [5 x i8] c"%ld\0A\00", align 1
@.print = private unnamed_addr constant [5 x i8] c"%ld \00", align 1
@.read = private unnamed_addr constant [4 x i8] c"%ld\00", align 1
@.read_scratch = common global i32 0, align 8
