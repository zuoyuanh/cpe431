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
	%u1 = call i8* @malloc(i32 24)
	%u2 = bitcast i8* %u1 to %struct.foo*
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
	%u30 = getelementptr %struct.foo* %u29, i1 0, i32 0
	%u31 = load i32* %u30
	%u33 = getelementptr %struct.foo* %u32, i1 0, i32 0
	%u34 = load i32* %u33
	%u35 = mul i32 %u31, %u34
	%u36 = getelementptr %struct.foo* %u29, i1 0, i32 2
	%u37 = load %struct.simple** %u36
	%u38 = getelementptr %struct.simple* %u37, i1 0, i32 0
	%u39 = load i32* %u38
	%u40 = mul i32 %u35, %u39
	%u41 = getelementptr %struct.foo* %u32, i1 0, i32 0
	%u42 = load i32* %u41
	%u43 = sdiv i32 %u40, %u42
	%u44 = getelementptr %struct.foo* %u32, i1 0, i32 2
	%u45 = load %struct.simple** %u44
	%u46 = getelementptr %struct.simple* %u45, i1 0, i32 0
	%u47 = load i32* %u46
	%u48 = getelementptr %struct.foo* %u29, i1 0, i32 0
	%u49 = load i32* %u48
	%u50 = call i32 @add(i32 %u47, i32 %u49)
	%u51 = getelementptr %struct.foo* %u32, i1 0, i32 0
	%u52 = load i32* %u51
	%u53 = getelementptr %struct.foo* %u29, i1 0, i32 0
	%u54 = load i32* %u53
	%u55 = sub i32 %u52, %u54
	%u57 = sub i32 %u56, 1
	%u58 = icmp sgt i32 %u57, 0
	br i1 %u58, label %LU9, label %LU10
LU10: 
	%u60 = phi %struct.foo* [%u12, %LU8], [%u32, %LU9]
	%u59 = phi %struct.foo* [%u7, %LU8], [%u29, %LU9]
		%u61 = bitcast %struct.foo* %u59 to i8*
	call void @free(i8* %u61)
		%u62 = bitcast %struct.foo* %u60 to i8*
	call void @free(i8* %u62)
	br label %LU7
LU7: 
	ret void
}

define void @objinstantiation(i32 %num)
{
LU12: 
	%u63 = icmp sgt i32 %num, 0
	br i1 %u63, label %LU13, label %LU14
LU13: 
	%u66 = phi i32 [%num, %LU12], [%u67, %LU13]
	%u64 = call i8* @malloc(i32 24)
	%u65 = bitcast i8* %u64 to %struct.foo*
		%u69 = bitcast %struct.foo* %u65 to i8*
	call void @free(i8* %u69)
	%u67 = sub i32 %u66, 1
	%u68 = icmp sgt i32 %u67, 0
	br i1 %u68, label %LU13, label %LU14
LU14: 
	br label %LU11
LU11: 
	ret void
}

define i32 @ackermann(i32 %m, i32 %n)
{
LU16: 
	%u70 = icmp eq i32 %m, 0
	br i1 %u70, label %LU17, label %LU18
LU17: 
	%u71 = add i32 %n, 1
		br label %LU15
LU18: 
	br label %LU19
LU19: 
	%u72 = icmp eq i32 %n, 0
	br i1 %u72, label %LU20, label %LU21
LU20: 
	%u73 = sub i32 %m, 1
	%u74 = call i32 @ackermann(i32 %u73, i32 1)
		br label %LU15
LU21: 
	%u75 = sub i32 %m, 1
	%u76 = sub i32 %n, 1
	%u77 = call i32 @ackermann(i32 %m, i32 %u76)
	%u78 = call i32 @ackermann(i32 %u75, i32 %u77)
		br label %LU15
LU15: 
	%u79 = phi i32 [%u71, %LU17], [%u74, %LU20], [%u78, %LU21]
	ret i32 %u79
}

define i32 @main()
{
LU24: 
	call i32 (i8*, ...)* @scanf(i8* getelementptr inbounds ([4 x i8]* @.read, i32 0, i32 0), i32* @.read_scratch)
	%u80 = load i32* @.read_scratch
	call i32 (i8*, ...)* @scanf(i8* getelementptr inbounds ([4 x i8]* @.read, i32 0, i32 0), i32* @.read_scratch)
	%u81 = load i32* @.read_scratch
	call i32 (i8*, ...)* @scanf(i8* getelementptr inbounds ([4 x i8]* @.read, i32 0, i32 0), i32* @.read_scratch)
	%u82 = load i32* @.read_scratch
	call i32 (i8*, ...)* @scanf(i8* getelementptr inbounds ([4 x i8]* @.read, i32 0, i32 0), i32* @.read_scratch)
	%u83 = load i32* @.read_scratch
	call i32 (i8*, ...)* @scanf(i8* getelementptr inbounds ([4 x i8]* @.read, i32 0, i32 0), i32* @.read_scratch)
	%u84 = load i32* @.read_scratch
	call void @tailrecursive(i32 %u80)
	call i32 (i8*, ...)* @printf(i8* getelementptr inbounds ([5 x i8]* @.println, i32 0, i32 0), i32 %u80)
	call void @domath(i32 %u81)
	call i32 (i8*, ...)* @printf(i8* getelementptr inbounds ([5 x i8]* @.println, i32 0, i32 0), i32 %u81)
	call void @objinstantiation(i32 %u82)
	call i32 (i8*, ...)* @printf(i8* getelementptr inbounds ([5 x i8]* @.println, i32 0, i32 0), i32 %u82)
	%u85 = call i32 @ackermann(i32 %u83, i32 %u84)
	call i32 (i8*, ...)* @printf(i8* getelementptr inbounds ([5 x i8]* @.println, i32 0, i32 0), i32 %u85)
		br label %LU23
LU23: 
	%u86 = phi i32 [0, %LU24]
	ret i32 %u86
}

declare i8* @malloc(i32)
declare void @free(i8*)
declare i32 @printf(i8*, ...)
declare i32 @scanf(i8*, ...)
@.println = private unnamed_addr constant [5 x i8] c"%ld\0A\00", align 1
@.print = private unnamed_addr constant [5 x i8] c"%ld \00", align 1
@.read = private unnamed_addr constant [4 x i8] c"%ld\00", align 1
@.read_scratch = common global i32 0, align 8
