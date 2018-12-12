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
	ret i32 %u4
}

define void @domath(i32 %num)
{
LU8: 
	%u7 = call i8* @malloc(i32 12)
	%u8 = bitcast i8* %u7 to %struct.foo*
	%u9 = getelementptr %struct.foo* %u8, i1 0, i32 2
	%u10 = call i8* @malloc(i32 4)
	%u11 = bitcast i8* %u10 to %struct.simple*
	store %struct.simple* %u11, %struct.simple** %u9
	%u12 = call i8* @malloc(i32 12)
	%u13 = bitcast i8* %u12 to %struct.foo*
	%u14 = getelementptr %struct.foo* %u13, i1 0, i32 2
	%u15 = call i8* @malloc(i32 4)
	%u16 = bitcast i8* %u15 to %struct.simple*
	store %struct.simple* %u16, %struct.simple** %u14
	%u17 = getelementptr %struct.foo* %u8, i1 0, i32 0
	store i32 %num, i32* %u17
	%u18 = getelementptr %struct.foo* %u13, i1 0, i32 0
	store i32 3, i32* %u18
	%u19 = getelementptr %struct.foo* %u8, i1 0, i32 2
	%u20 = load %struct.simple** %u19
	%u21 = getelementptr %struct.simple* %u20, i1 0, i32 0
	%u22 = getelementptr %struct.foo* %u8, i1 0, i32 0
	%u23 = load i32* %u22
	store i32 %u23, i32* %u21
	%u24 = getelementptr %struct.foo* %u13, i1 0, i32 2
	%u25 = load %struct.simple** %u24
	%u26 = getelementptr %struct.simple* %u25, i1 0, i32 0
	%u27 = getelementptr %struct.foo* %u13, i1 0, i32 0
	%u28 = load i32* %u27
	store i32 %u28, i32* %u26
	%u29 = icmp sgt i32 %num, 0
	br i1 %u29, label %LU9, label %LU10
LU9: 
	%u58 = phi i32 [%num, %LU8], [%u59, %LU9]
	%u34 = phi %struct.foo* [%u13, %LU8], [%u34, %LU9]
	%u31 = phi %struct.foo* [%u8, %LU8], [%u31, %LU9]
	%u46 = getelementptr %struct.foo* %u34, i1 0, i32 2
	%u47 = load %struct.simple** %u46
	%u48 = getelementptr %struct.simple* %u47, i1 0, i32 0
	%u49 = load i32* %u48
	%u50 = getelementptr %struct.foo* %u31, i1 0, i32 0
	%u51 = load i32* %u50
		%u52 = call i32 @add(i32 %u49, i32 %u51)
	%u59 = sub i32 %u58, 1
	%u60 = icmp sgt i32 %u59, 0
	br i1 %u60, label %LU9, label %LU10
LU10: 
	%u62 = phi %struct.foo* [%u13, %LU8], [%u34, %LU9]
	%u61 = phi %struct.foo* [%u8, %LU8], [%u31, %LU9]
		%u69 = bitcast %struct.foo* %u61 to i8*
	call void @free(i8* %u69)
		%u70 = bitcast %struct.foo* %u62 to i8*
	call void @free(i8* %u70)
	br label %LU7
LU7: 
	ret void
}

define void @objinstantiation(i32 %num)
{
LU12: 
	%u71 = icmp sgt i32 %num, 0
	br i1 %u71, label %LU13, label %LU14
LU13: 
	%u75 = phi i32 [%num, %LU12], [%u76, %LU13]
	%u73 = call i8* @malloc(i32 12)
	%u74 = bitcast i8* %u73 to %struct.foo*
		%u80 = bitcast %struct.foo* %u74 to i8*
	call void @free(i8* %u80)
	%u76 = sub i32 %u75, 1
	%u77 = icmp sgt i32 %u76, 0
	br i1 %u77, label %LU13, label %LU14
LU14: 
	br label %LU11
LU11: 
	ret void
}

define i32 @ackermann(i32 %m, i32 %n)
{
LU16: 
	%u81 = icmp eq i32 %m, 0
	br i1 %u81, label %LU17, label %LU18
LU17: 
	%u82 = add i32 %n, 1
	br label %LU15
LU18: 
	br label %LU19
LU19: 
	%u83 = icmp eq i32 %n, 0
	br i1 %u83, label %LU20, label %LU21
LU20: 
	%u84 = sub i32 %m, 1
		%u85 = call i32 @ackermann(i32 %u84, i32 1)
	br label %LU15
LU21: 
	%u86 = sub i32 %m, 1
	%u87 = sub i32 %n, 1
		%u88 = call i32 @ackermann(i32 %m, i32 %u87)
		%u89 = call i32 @ackermann(i32 %u86, i32 %u88)
	br label %LU15
LU15: 
	%u90 = phi i32 [%u82, %LU17], [%u85, %LU20], [%u89, %LU21]
	ret i32 %u90
}

define i32 @main()
{
LU24: 
	call i32 (i8*, ...)* @scanf(i8* getelementptr inbounds ([4 x i8]* @.read, i32 0, i32 0), i32* @.read_scratch)
	%u92 = load i32* @.read_scratch
	call i32 (i8*, ...)* @scanf(i8* getelementptr inbounds ([4 x i8]* @.read, i32 0, i32 0), i32* @.read_scratch)
	%u93 = load i32* @.read_scratch
	call i32 (i8*, ...)* @scanf(i8* getelementptr inbounds ([4 x i8]* @.read, i32 0, i32 0), i32* @.read_scratch)
	%u94 = load i32* @.read_scratch
	call i32 (i8*, ...)* @scanf(i8* getelementptr inbounds ([4 x i8]* @.read, i32 0, i32 0), i32* @.read_scratch)
	%u95 = load i32* @.read_scratch
	call i32 (i8*, ...)* @scanf(i8* getelementptr inbounds ([4 x i8]* @.read, i32 0, i32 0), i32* @.read_scratch)
	%u96 = load i32* @.read_scratch
		call void @tailrecursive(i32 %u92)
	call i32 (i8*, ...)* @printf(i8* getelementptr inbounds ([5 x i8]* @.println, i32 0, i32 0), i32 %u92)
		call void @domath(i32 %u93)
	call i32 (i8*, ...)* @printf(i8* getelementptr inbounds ([5 x i8]* @.println, i32 0, i32 0), i32 %u93)
		call void @objinstantiation(i32 %u94)
	call i32 (i8*, ...)* @printf(i8* getelementptr inbounds ([5 x i8]* @.println, i32 0, i32 0), i32 %u94)
		%u97 = call i32 @ackermann(i32 %u95, i32 %u96)
	call i32 (i8*, ...)* @printf(i8* getelementptr inbounds ([5 x i8]* @.println, i32 0, i32 0), i32 %u97)
	br label %LU23
LU23: 
	ret i32 0
}

declare i8* @malloc(i32)
declare void @free(i8*)
declare i32 @printf(i8*, ...)
declare i32 @scanf(i8*, ...)
@.println = private unnamed_addr constant [5 x i8] c"%ld\0A\00", align 1
@.print = private unnamed_addr constant [5 x i8] c"%ld \00", align 1
@.read = private unnamed_addr constant [4 x i8] c"%ld\00", align 1
@.read_scratch = common global i32 0, align 8
