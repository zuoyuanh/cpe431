	.arch armv7-a
@globalfoo = common global %struct.foo* null, align 8

	.align 2
	.global tailrecursive
tailrecursive:
.LU1: 
	mov %u0, #0
	mov %u4, #0
	cmp %num, %u4
	movle %u0, #1
	cmp %u0, #1
	beq .LU2
	b .LU3
.LU2: 
	b .LU0
.LU3: 
	b .LU4
.LU4: 
	sub %u3, %num, #1
	b .LU0
.LU0: 
}

	.align 2
	.global add
add:
.LU6: 
	b .LU5
.LU5: 
}

	.align 2
	.global domath
domath:
.LU8: 
	mov %u9, %u8
	mov %r0, #24
	bl malloc
	mov %u8, %r0
	add %u10, %u9, #8
	mov %u12, %u11
	mov %r0, #8
	bl malloc
	mov %u11, %r0
	str %u12, [%u10]
	mov %u14, %u13
	mov %r0, #24
	bl malloc
	mov %u13, %r0
	add %u15, %u14, #8
	mov %u17, %u16
	mov %r0, #8
	bl malloc
	mov %u16, %r0
	str %u17, [%u15]
	add %u18, %u9, #0
	str %num, [%u18]
	add %u19, %u14, #0
	mov %u68, #3
	str %u68, [%u19]
	add %u20, %u9, #8
	ldr %u21, [%u20]
	add %u22, %u21, #0
	add %u23, %u9, #0
	ldr %u24, [%u23]
	str %u24, [%u22]
	add %u25, %u14, #8
	ldr %u26, [%u25]
	add %u27, %u26, #0
	add %u28, %u14, #0
	ldr %u29, [%u28]
	str %u29, [%u27]
	mov %u30, #0
	mov %u69, #0
	cmp %num, %u69
	movgt %u30, #1
	cmp %u30, #1
	beq .LU9
	b .LU10
.LU9: 
	mov %u58, %u65
	mov %u34, %u64
	mov %u31, %u63
	add %u46, %u34, #8
	ldr %u47, [%u46]
	add %u48, %u47, #0
	ldr %u49, [%u48]
	add %u50, %u31, #0
	ldr %u51, [%u50]
	sub %u59, %u58, #1
	mov %u60, #0
	mov %u70, #0
	cmp %u59, %u70
	movgt %u60, #1
	cmp %u60, #1
	beq .LU9
	b .LU10
.LU10: 
	b .LU7
}

	.align 2
	.global objinstantiation
objinstantiation:
.LU12: 
	mov %u71, #0
	mov %u79, #0
	cmp %num, %u79
	movgt %u71, #1
	cmp %u71, #1
	beq .LU13
	b .LU14
.LU13: 
	mov %u74, %u77
	sub %u75, %u74, #1
	mov %u76, #0
	mov %u80, #0
	cmp %u75, %u80
	movgt %u76, #1
	cmp %u76, #1
	beq .LU13
	b .LU14
.LU14: 
	b .LU11
}

	.align 2
	.global ackermann
ackermann:
.LU16: 
	mov %u81, #0
	mov %u92, #0
	cmp %m, %u92
	moveq %u81, #1
	cmp %u81, #1
	beq .LU17
	b .LU18
.LU17: 
	b .LU15
.LU18: 
	b .LU19
.LU19: 
	mov %u83, #0
	mov %u93, #0
	cmp %n, %u93
	moveq %u83, #1
	cmp %u83, #1
	beq .LU20
	b .LU21
.LU20: 
	sub %u84, %m, #1
	b .LU15
.LU21: 
	sub %u86, %m, #1
	sub %u87, %n, #1
	b .LU15
.LU15: 
	mov %u90, %u91
}

	.align 2
	.global main
main:
.LU24: 
	movw %r1, #:lower16:.read_scratch
	movt %r1, #:upper16:.read_scratch
	movw %r0, #:lower16:.READ_FMT
	movt %r0, #:upper16:.READ_FMT
	bl scanf
	movw %u94, #:lower16:.read_scratch
	movt %u94, #:upper16:.read_scratch
	ldr %u94, [%u94]
	movw %r1, #:lower16:.read_scratch
	movt %r1, #:upper16:.read_scratch
	movw %r0, #:lower16:.READ_FMT
	movt %r0, #:upper16:.READ_FMT
	bl scanf
	movw %u95, #:lower16:.read_scratch
	movt %u95, #:upper16:.read_scratch
	ldr %u95, [%u95]
	movw %r1, #:lower16:.read_scratch
	movt %r1, #:upper16:.read_scratch
	movw %r0, #:lower16:.READ_FMT
	movt %r0, #:upper16:.READ_FMT
	bl scanf
	movw %u96, #:lower16:.read_scratch
	movt %u96, #:upper16:.read_scratch
	ldr %u96, [%u96]
	movw %r1, #:lower16:.read_scratch
	movt %r1, #:upper16:.read_scratch
	movw %r0, #:lower16:.READ_FMT
	movt %r0, #:upper16:.READ_FMT
	bl scanf
	movw %u97, #:lower16:.read_scratch
	movt %u97, #:upper16:.read_scratch
	ldr %u97, [%u97]
	movw %r1, #:lower16:.read_scratch
	movt %r1, #:upper16:.read_scratch
	movw %r0, #:lower16:.READ_FMT
	movt %r0, #:upper16:.READ_FMT
	bl scanf
	movw %u98, #:lower16:.read_scratch
	movt %u98, #:upper16:.read_scratch
	ldr %u98, [%u98]
	mov %r1, %u94
	movw %r0, #:lower16:.PRINTLN_FMT
	movt %r0, #:upper16:.PRINTLN_FMT
	bl printf
	mov %r1, %u95
	movw %r0, #:lower16:.PRINTLN_FMT
	movt %r0, #:upper16:.PRINTLN_FMT
	bl printf
	mov %r1, %u96
	movw %r0, #:lower16:.PRINTLN_FMT
	movt %r0, #:upper16:.PRINTLN_FMT
	bl printf
	mov %r1, %u99
	movw %r0, #:lower16:.PRINTLN_FMT
	movt %r0, #:upper16:.PRINTLN_FMT
	bl printf
	b .LU23
.LU23: 
}

	.section	.rodata
	.align	2
.PRINTLN_FMT:
	.asciz	"%ld"
	.align	2
.PRINT_FMT:
	.asciz	"%ld "
	.align	2
.READ_FMT:
	.asciz	"%ld"
	.comm	.read_scratch,4,4
	.global	__aeabi_idiv
