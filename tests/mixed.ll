	.arch armv7-a
@globalfoo = common global %struct.foo* null, align 8

	.align 2
	.global tailrecursive
tailrecursive:
.LU1: 
	push {%fp, %lr}
	add %fp, %sp, #4
	mov %num, %r0
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
	mov %r0, %u3
	bl tailrecursive
	b .LU0
.LU0: 
	pop {%fp, %pc}
	.size tailrecursive, .-tailrecursive
	.align 2
	.global add
add:
.LU6: 
	push {%fp, %lr}
	add %fp, %sp, #4
	mov %x, %r0
	mov %y, %r2
	add %u5, %x, %y
	b .LU5
.LU5: 
	mov %r0, %u5
	pop {%fp, %pc}
	.size add, .-add
	.align 2
	.global domath
domath:
.LU8: 
	push {%fp, %lr}
	add %fp, %sp, #4
	mov %num, %r0
	mov %u65, %num
	mov %r0, #12
	bl malloc
	mov %u8, %r0
	mov %u9, %u8
	mov %u66, %u9
	mov %u63, %u9
	add %u10, %u9, #8
	mov %r0, #4
	bl malloc
	mov %u11, %r0
	mov %u12, %u11
	str %u12, [%u10]
	mov %r0, #12
	bl malloc
	mov %u13, %r0
	mov %u14, %u13
	mov %u67, %u14
	mov %u64, %u14
	add %u15, %u14, #8
	mov %r0, #4
	bl malloc
	mov %u16, %r0
	mov %u17, %u16
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
	mov %u67, %u34
	mov %u64, %u34
	mov %u31, %u63
	mov %u66, %u31
	mov %u63, %u31
	add %u46, %u34, #8
	ldr %u47, [%u46]
	add %u48, %u47, #0
	ldr %u49, [%u48]
	add %u50, %u31, #0
	ldr %u51, [%u50]
	mov %r1, %u51
	mov %r0, %u49
	bl add
	mov %u52, %r0
	sub %u59, %u58, #1
	mov %u65, %u59
	mov %u60, #0
	mov %u70, #0
	cmp %u59, %u70
	movgt %u60, #1
	cmp %u60, #1
	beq .LU9
	b .LU10
.LU10: 
	mov %u62, %u67
	mov %u61, %u66
	mov %r0, %u61
	bl free
	mov %r0, %u62
	bl free
	b .LU7
.LU7: 
	pop {%fp, %pc}
	.size domath, .-domath
	.align 2
	.global objinstantiation
objinstantiation:
.LU12: 
	push {%fp, %lr}
	add %fp, %sp, #4
	mov %num, %r0
	mov %u77, %num
	mov %u71, #0
	mov %u78, #0
	cmp %num, %u78
	movgt %u71, #1
	cmp %u71, #1
	beq .LU13
	b .LU14
.LU13: 
	mov %u74, %u77
	mov %r0, #12
	bl malloc
	mov %u72, %r0
	mov %u73, %u72
	mov %r0, %u73
	bl free
	sub %u75, %u74, #1
	mov %u77, %u75
	mov %u76, #0
	mov %u79, #0
	cmp %u75, %u79
	movgt %u76, #1
	cmp %u76, #1
	beq .LU13
	b .LU14
.LU14: 
	b .LU11
.LU11: 
	pop {%fp, %pc}
	.size objinstantiation, .-objinstantiation
	.align 2
	.global ackermann
ackermann:
.LU16: 
	push {%fp, %lr}
	add %fp, %sp, #4
	mov %m, %r0
	mov %n, %r2
	mov %u80, #0
	mov %u91, #0
	cmp %m, %u91
	moveq %u80, #1
	cmp %u80, #1
	beq .LU17
	b .LU18
.LU17: 
	add %u81, %n, #1
	mov %u90, %u81
	b .LU15
.LU18: 
	b .LU19
.LU19: 
	mov %u82, #0
	mov %u92, #0
	cmp %n, %u92
	moveq %u82, #1
	cmp %u82, #1
	beq .LU20
	b .LU21
.LU20: 
	sub %u83, %m, #1
	mov %r1, #1
	mov %r0, %u83
	bl ackermann
	mov %u84, %r0
	mov %u90, %u84
	b .LU15
.LU21: 
	sub %u85, %m, #1
	sub %u86, %n, #1
	mov %r1, %u86
	mov %r0, %m
	bl ackermann
	mov %u87, %r0
	mov %r1, %u87
	mov %r0, %u85
	bl ackermann
	mov %u88, %r0
	mov %u90, %u88
	b .LU15
.LU15: 
	mov %u89, %u90
	mov %r0, %u89
	pop {%fp, %pc}
	.size ackermann, .-ackermann
	.align 2
	.global main
main:
.LU24: 
	push {%fp, %lr}
	add %fp, %sp, #4
	movw %r1, #:lower16:.read_scratch
	movt %r1, #:upper16:.read_scratch
	movw %r0, #:lower16:.READ_FMT
	movt %r0, #:upper16:.READ_FMT
	bl scanf
	movw %u93, #:lower16:.read_scratch
	movt %u93, #:upper16:.read_scratch
	ldr %u93, [%u93]
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
	mov %r0, %u93
	bl tailrecursive
	mov %r1, %u93
	movw %r0, #:lower16:.PRINTLN_FMT
	movt %r0, #:upper16:.PRINTLN_FMT
	bl printf
	mov %r0, %u94
	bl domath
	mov %r1, %u94
	movw %r0, #:lower16:.PRINTLN_FMT
	movt %r0, #:upper16:.PRINTLN_FMT
	bl printf
	mov %r0, %u95
	bl objinstantiation
	mov %r1, %u95
	movw %r0, #:lower16:.PRINTLN_FMT
	movt %r0, #:upper16:.PRINTLN_FMT
	bl printf
	mov %r1, %u97
	mov %r0, %u96
	bl ackermann
	mov %u98, %r0
	mov %r1, %u98
	movw %r0, #:lower16:.PRINTLN_FMT
	movt %r0, #:upper16:.PRINTLN_FMT
	bl printf
	b .LU23
.LU23: 
	mov %r0, #0
	pop {%fp, %pc}
	.size main, .-main
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
