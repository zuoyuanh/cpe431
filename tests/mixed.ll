target triple="i686"
%struct.simple = type {i32}
%struct.foo = type {i32, i32, %struct.simple*}

@globalfoo = common global %struct.foo* null, align 8

define void @tailrecursive(i32 %num)
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

define i32 @add(i32 %x, i32 %y)
{
LU6: 
	%u8 = add i32 %x, %y
	br label %LU5
LU5: 
	%u9 = phi i32 [%u8, %LU6]
	ret i32 %u9
}

define void @domath(i32 %num)
{
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
	%u28 = getelementptr %struct.foo* %u11, i1 0, i32 2
	%u29 = load %struct.simple** %u28
	%u30 = getelementptr %struct.simple* %u29, i1 0, i32 0
	%u32 = getelementptr %struct.foo* %u11, i1 0, i32 0
	%u33 = load i32* %u32
	store i32 %u33, i32* %u30
	%u35 = getelementptr %struct.foo* %u17, i1 0, i32 2
	%u36 = load %struct.simple** %u35
	%u37 = getelementptr %struct.simple* %u36, i1 0, i32 0
	%u39 = getelementptr %struct.foo* %u17, i1 0, i32 0
	%u40 = load i32* %u39
	store i32 %u40, i32* %u37
	%u42 = icmp sgt i32 %num, 0
	br i1 %u42, label %LU9, label %LU10
LU9: 
	%u44 = phi %struct.foo* [%u11, %LU8], [%u44, %LU9]
	%u48 = phi %struct.foo* [%u17, %LU8], [%u48, %LU9]
	%u80 = phi i32 [%num, %LU8], [%u81, %LU9]
	%u45 = getelementptr %struct.foo* %u44, i1 0, i32 0
	%u46 = load i32* %u45
	%u49 = getelementptr %struct.foo* %u48, i1 0, i32 0
	%u50 = load i32* %u49
	%u51 = mul i32 %u46, %u50
	%u54 = getelementptr %struct.foo* %u44, i1 0, i32 2
	%u55 = load %struct.simple** %u54
	%u56 = getelementptr %struct.simple* %u55, i1 0, i32 0
	%u57 = load i32* %u56
	%u58 = mul i32 %u51, %u57
	%u60 = getelementptr %struct.foo* %u48, i1 0, i32 0
	%u61 = load i32* %u60
	%u62 = sdiv i32 %u58, %u61
	%u64 = getelementptr %struct.foo* %u48, i1 0, i32 2
	%u65 = load %struct.simple** %u64
	%u66 = getelementptr %struct.simple* %u65, i1 0, i32 0
	%u67 = load i32* %u66
	%u69 = getelementptr %struct.foo* %u44, i1 0, i32 0
	%u70 = load i32* %u69
	%u71 = call i32 @add (i32 %u67, i32 %u70)
	%u73 = getelementptr %struct.foo* %u48, i1 0, i32 0
	%u74 = load i32* %u73
	%u76 = getelementptr %struct.foo* %u44, i1 0, i32 0
	%u77 = load i32* %u76
	%u78 = sub i32 %u74, %u77
	%u81 = sub i32 %u80, 1
	%u83 = icmp sgt i32 %u81, 0
	br i1 %u83, label %LU9, label %LU10
LU10: 
	%u85 = phi %struct.foo* [%u11, %LU8], [%u44, %LU9]
	%u88 = phi %struct.foo* [%u17, %LU8], [%u48, %LU9]
	%u86 = bitcast %struct.foo* %u85 to i8*
	call void @free(i8* %u86)
	%u89 = bitcast %struct.foo* %u88 to i8*
	call void @free(i8* %u89)
	br label %LU7
LU7: 
	ret void
}

define void @objinstantiation(i32 %num)
{
LU12: 
	%u91 = icmp sgt i32 %num, 0
	br i1 %u91, label %LU13, label %LU14
LU13: 
	%u97 = phi i32 [%num, %LU12], [%u98, %LU13]
	%u92 = call i8* @malloc(i32 24)
	%u93 = bitcast i8* %u92 to %struct.foo*
	%u95 = bitcast %struct.foo* %u93 to i8*
	call void @free(i8* %u95)
	%u98 = sub i32 %u97, 1
	%u100 = icmp sgt i32 %u98, 0
	br i1 %u100, label %LU13, label %LU14
LU14: 
	br label %LU11
LU11: 
	ret void
}

define i32 @ackermann(i32 %m, i32 %n)
{
LU16: 
	%u102 = icmp eq i32 %m, 0
	br i1 %u102, label %LU17, label %LU18
LU17: 
	%u104 = add i32 %n, 1
	br label %LU15
LU18: 
	br label %LU19
LU19: 
	%u106 = icmp eq i32 %n, 0
	br i1 %u106, label %LU20, label %LU21
LU20: 
	%u108 = sub i32 %m, 1
	%u109 = call i32 @ackermann (i32 %u108, i32 1)
	br label %LU15
LU21: 
	%u111 = sub i32 %m, 1
	%u114 = sub i32 %n, 1
	%u115 = call i32 @ackermann (i32 %m, i32 %u114)
	%u116 = call i32 @ackermann (i32 %u111, i32 %u115)
	br label %LU15
LU15: 
	%u117 = phi i32 [%u104, %LU17], [%u109, %LU20], [%u116, %LU21]
	ret i32 %u117
}

define i32 @main()
{
LU24: 
	call i32 (i8*, ...)* @scanf(i8* getelementptr inbounds ([4 x i8]* @.read, i32 0, i32 0), i32* @.read_scratch)
	%u118 = load i32* @.read_scratch
	call i32 (i8*, ...)* @scanf(i8* getelementptr inbounds ([4 x i8]* @.read, i32 0, i32 0), i32* @.read_scratch)
	%u119 = load i32* @.read_scratch
	call i32 (i8*, ...)* @scanf(i8* getelementptr inbounds ([4 x i8]* @.read, i32 0, i32 0), i32* @.read_scratch)
	%u120 = load i32* @.read_scratch
	call i32 (i8*, ...)* @scanf(i8* getelementptr inbounds ([4 x i8]* @.read, i32 0, i32 0), i32* @.read_scratch)
	%u121 = load i32* @.read_scratch
	call i32 (i8*, ...)* @scanf(i8* getelementptr inbounds ([4 x i8]* @.read, i32 0, i32 0), i32* @.read_scratch)
	%u122 = load i32* @.read_scratch
	call void @tailrecursive(i32 %u118)
	call i32 (i8*, ...)* @printf(i8* getelementptr inbounds ([5 x i8]* @.println, i32 0, i32 0), i32 %u118)
	call void @domath(i32 %u119)
	call i32 (i8*, ...)* @printf(i8* getelementptr inbounds ([5 x i8]* @.println, i32 0, i32 0), i32 %u119)
	call void @objinstantiation(i32 %u120)
	call i32 (i8*, ...)* @printf(i8* getelementptr inbounds ([5 x i8]* @.println, i32 0, i32 0), i32 %u120)
	%u131 = call i32 @ackermann (i32 %u121, i32 %u122)
	call i32 (i8*, ...)* @printf(i8* getelementptr inbounds ([5 x i8]* @.println, i32 0, i32 0), i32 %u131)
	br label %LU23
LU23: 
	%u132 = phi i32 [0, %LU24]
	ret i32 %u132
}

declare i8* @malloc(i32)
declare void @free(i8*)
declare i32 @printf(i8*, ...)
declare i32 @scanf(i8*, ...)
@.println = private unnamed_addr constant [5 x i8] c"%ld\0A\00", align 1
@.print = private unnamed_addr constant [5 x i8] c"%ld \00", align 1
@.read = private unnamed_addr constant [4 x i8] c"%ld\00", align 1
@.read_scratch = common global i32 0, align 8
