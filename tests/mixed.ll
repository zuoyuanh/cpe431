	.arch armv7-a
	.comm   globalfoo,4,4

	.text
	.align 2
	.global tailrecursive
tailrecursive:
.LU1: 
	push {fp, lr}
	add fp, sp, #4
	mov r1, r0
	mov r0, #0
	mov r0, #0
	cmp r1, r0
	movle r0, #1
	cmp r0, #1
	beq .LU2
	b .LU3
.LU2: 
	b .LU0
.LU3: 
	b .LU4
.LU4: 
	sub r0, r1, #1
	bl tailrecursive
	b .LU0
.LU0: 
	pop {fp, pc}
	.size tailrecursive, .-tailrecursive
	.align 2
	.global add
add:
.LU6: 
	push {fp, lr}
	add fp, sp, #4
	mov r2, r0
	add r0, r2, r0
	b .LU5
.LU5: 
	pop {fp, pc}
	.size add, .-add
	.align 2
	.global domath
domath:
.LU8: 
	push {fp, lr}
	add fp, sp, #4
	push {r4, r5, r6, r7, r8}
	sub sp, sp, #8
	mov r4, r0
	mov r5, r4
	movw r0, #12
	bl malloc
	mov r2, r0
	mov r10, r2
	str r10, [fp, #-8]
	mov r8, r2
	add r1, r2, #8
	movw r0, #4
	bl malloc
	str r0, [r1]
	movw r0, #12
	bl malloc
	mov r3, r0
	mov r6, r3
	mov r7, r3
	add r1, r3, #8
	movw r0, #4
	bl malloc
	str r0, [r1]
	add r0, r2, #0
	str r4, [r0]
	add r0, r3, #0
	mov r1, #3
	str r1, [r0]
	add r0, r2, #8
	ldr r0, [r0]
	add r1, r0, #0
	add r0, r2, #0
	ldr r0, [r0]
	str r0, [r1]
	add r0, r3, #8
	ldr r0, [r0]
	add r1, r0, #0
	add r0, r3, #0
	ldr r0, [r0]
	str r0, [r1]
	mov r0, #0
	mov r0, #0
	cmp r4, r0
	movgt r0, #1
	cmp r0, #1
	beq .LU9
	b .LU10
.LU9: 
	mov r3, r5
	mov r0, r7
	mov r6, r0
	mov r7, r0
	mov r1, r8
	mov r10, r1
	str r10, [fp, #-8]
	mov r8, r1
	add r0, r0, #8
	ldr r0, [r0]
	add r0, r0, #0
	ldr r2, [r0]
	add r0, r1, #0
	ldr r0, [r0]
	mov r0, r2
	bl add
	sub r1, r3, #1
	mov r5, r1
	mov r0, #0
	mov r0, #0
	cmp r1, r0
	movgt r0, #1
	cmp r0, #1
	beq .LU9
	b .LU10
.LU10: 
	mov r1, r6
	ldr r9, [fp, #-8]
	mov r0, r9
	bl free
	mov r0, r1
	bl free
	b .LU7
.LU7: 
	pop {r4, r5, r6, r7, r8}
	pop {fp, pc}
	.size domath, .-domath
	.align 2
	.global objinstantiation
objinstantiation:
.LU12: 
	push {fp, lr}
	add fp, sp, #4
	mov r1, r0
	mov r2, r1
	mov r0, #0
	mov r0, #0
	cmp r1, r0
	movgt r0, #1
	cmp r0, #1
	beq .LU13
	b .LU14
.LU13: 
	mov r1, r2
	movw r0, #12
	bl malloc
	bl free
	sub r1, r1, #1
	mov r2, r1
	mov r0, #0
	mov r0, #0
	cmp r1, r0
	movgt r0, #1
	cmp r0, #1
	beq .LU13
	b .LU14
.LU14: 
	b .LU11
.LU11: 
	pop {fp, pc}
	.size objinstantiation, .-objinstantiation
	.align 2
	.global ackermann
ackermann:
.LU16: 
	push {fp, lr}
	add fp, sp, #4
	mov r3, r0
	mov r0, #0
	mov r0, #0
	cmp r3, r0
	moveq r0, #1
	cmp r0, #1
	beq .LU17
	b .LU18
.LU17: 
	add r0, r1, #1
	b .LU15
.LU18: 
	b .LU19
.LU19: 
	mov r0, #0
	mov r0, #0
	cmp r1, r0
	moveq r0, #1
	cmp r0, #1
	beq .LU20
	b .LU21
.LU20: 
	sub r0, r3, #1
	mov r1, #1
	bl ackermann
	b .LU15
.LU21: 
	sub r2, r3, #1
	sub r0, r1, #1
	mov r1, r0
	mov r0, r3
	bl ackermann
	mov r1, r0
	mov r0, r2
	bl ackermann
	b .LU15
.LU15: 
	pop {fp, pc}
	.size ackermann, .-ackermann
	.align 2
	.global main
main:
.LU24: 
	push {fp, lr}
	add fp, sp, #4
	push {r4, r5, r6}
	movw r1, #:lower16:.read_scratch
	movt r1, #:upper16:.read_scratch
	movw r0, #:lower16:.READ_FMT
	movt r0, #:upper16:.READ_FMT
	bl scanf
	movw r5, #:lower16:.read_scratch
	movt r5, #:upper16:.read_scratch
	ldr r5, [r5]
	movw r1, #:lower16:.read_scratch
	movt r1, #:upper16:.read_scratch
	movw r0, #:lower16:.READ_FMT
	movt r0, #:upper16:.READ_FMT
	bl scanf
	movw r6, #:lower16:.read_scratch
	movt r6, #:upper16:.read_scratch
	ldr r6, [r6]
	movw r1, #:lower16:.read_scratch
	movt r1, #:upper16:.read_scratch
	movw r0, #:lower16:.READ_FMT
	movt r0, #:upper16:.READ_FMT
	bl scanf
	movw r3, #:lower16:.read_scratch
	movt r3, #:upper16:.read_scratch
	ldr r3, [r3]
	movw r1, #:lower16:.read_scratch
	movt r1, #:upper16:.read_scratch
	movw r0, #:lower16:.READ_FMT
	movt r0, #:upper16:.READ_FMT
	bl scanf
	movw r4, #:lower16:.read_scratch
	movt r4, #:upper16:.read_scratch
	ldr r4, [r4]
	movw r1, #:lower16:.read_scratch
	movt r1, #:upper16:.read_scratch
	movw r0, #:lower16:.READ_FMT
	movt r0, #:upper16:.READ_FMT
	bl scanf
	movw r2, #:lower16:.read_scratch
	movt r2, #:upper16:.read_scratch
	ldr r2, [r2]
	mov r0, r5
	bl tailrecursive
	mov r1, r5
	movw r0, #:lower16:.PRINTLN_FMT
	movt r0, #:upper16:.PRINTLN_FMT
	bl printf
	mov r0, r6
	bl domath
	mov r1, r6
	movw r0, #:lower16:.PRINTLN_FMT
	movt r0, #:upper16:.PRINTLN_FMT
	bl printf
	mov r0, r3
	bl objinstantiation
	mov r1, r3
	movw r0, #:lower16:.PRINTLN_FMT
	movt r0, #:upper16:.PRINTLN_FMT
	bl printf
	mov r1, r2
	mov r0, r4
	bl ackermann
	movw r0, #:lower16:.PRINTLN_FMT
	movt r0, #:upper16:.PRINTLN_FMT
	bl printf
	b .LU23
.LU23: 
	mov r0, #0
	pop {r4, r5, r6}
	pop {fp, pc}
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
