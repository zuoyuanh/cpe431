	.arch armv7-a
	.comm   globalfoo,4,4

	.text
	.align 2
	.global tailrecursive
tailrecursive:
.LU1: 
	push {fp, lr}
	add fp, sp, #4
	push {r4, r5, r6}
	mov r5, r0
	mov r6, #0
	mov r4, #0
	cmp r5, r4
	movle r6, #1
	cmp r6, #1
	beq .LU2
	b .LU3
.LU2: 
	b .LU0
.LU3: 
	b .LU4
.LU4: 
	sub r4, r5, #1
	mov r0, r4
	bl tailrecursive
	b .LU0
.LU0: 
	pop {r4, r5, r6}
	sub sp, fp, #4
	pop {fp, pc}
	.size tailrecursive, .-tailrecursive
	.align 2
	.global add
add:
.LU6: 
	push {fp, lr}
	add fp, sp, #4
	push {r4, r5}
	mov r5, r0
	mov r4, r1
	add r4, r5, r4
	b .LU5
.LU5: 
	mov r0, r4
	pop {r4, r5}
	sub sp, fp, #4
	pop {fp, pc}
	.size add, .-add
	.align 2
	.global domath
domath:
.LU8: 
	push {fp, lr}
	add fp, sp, #4
	push {r4, r5, r6, r7, r8}
	sub sp, sp, #24
	mov r7, r0
	movw r0, #12
	bl malloc
	mov r4, r0
	mov r10, r4
	str r10, [sp, #20]
	ldr r9, [sp, #20]
	add r5, r9, #8
	movw r0, #4
	bl malloc
	mov r4, r0
	str r4, [r5]
	movw r0, #12
	bl malloc
	mov r4, r0
	mov r10, r4
	str r10, [sp, #16]
	ldr r9, [sp, #16]
	add r5, r9, #8
	movw r0, #4
	bl malloc
	mov r4, r0
	str r4, [r5]
	ldr r9, [sp, #20]
	add r4, r9, #0
	str r7, [r4]
	ldr r9, [sp, #16]
	add r4, r9, #0
	mov r5, #3
	str r5, [r4]
	ldr r9, [sp, #20]
	add r4, r9, #8
	ldr r4, [r4]
	add r5, r4, #0
	ldr r9, [sp, #20]
	add r4, r9, #0
	ldr r4, [r4]
	str r4, [r5]
	ldr r9, [sp, #16]
	add r4, r9, #8
	ldr r4, [r4]
	add r5, r4, #0
	ldr r9, [sp, #16]
	add r4, r9, #0
	ldr r4, [r4]
	str r4, [r5]
	mov r6, #0
	mov r4, #0
	cmp r7, r4
	movgt r6, #1
	ldr r9, [sp, #16]
	mov r8, r9
	ldr r9, [sp, #20]
	mov r10, r9
	str r10, [sp, #4]
	ldr r9, [sp, #16]
	mov r5, r9
	ldr r9, [sp, #20]
	mov r4, r9
	cmp r6, #1
	beq .LU9
	b .LU10
.LU9: 
	mov r6, r7
	mov r10, r5
	str r10, [sp, #8]
	mov r10, r4
	str r10, [sp, #12]
	ldr r9, [sp, #8]
	add r4, r9, #8
	ldr r4, [r4]
	add r4, r4, #0
	ldr r5, [r4]
	ldr r9, [sp, #12]
	add r4, r9, #0
	ldr r4, [r4]
	mov r0, r5
	mov r1, r4
	bl add
	mov r4, r0
	sub r5, r6, #1
	mov r6, #0
	mov r4, #0
	cmp r5, r4
	movgt r6, #1
	ldr r9, [sp, #8]
	mov r8, r9
	ldr r9, [sp, #12]
	mov r10, r9
	str r10, [sp, #4]
	mov r7, r5
	ldr r9, [sp, #8]
	mov r5, r9
	ldr r9, [sp, #12]
	mov r4, r9
	cmp r6, #1
	beq .LU9
	b .LU10
.LU10: 
	mov r5, r8
	ldr r9, [sp, #4]
	mov r4, r9
	mov r0, r4
	bl free
	mov r0, r5
	bl free
	b .LU7
.LU7: 
	add sp, sp, #24
	pop {r4, r5, r6, r7, r8}
	sub sp, fp, #4
	pop {fp, pc}
	.size domath, .-domath
	.align 2
	.global objinstantiation
objinstantiation:
.LU12: 
	push {fp, lr}
	add fp, sp, #4
	push {r4, r5, r6}
	mov r5, r0
	mov r6, #0
	mov r4, #0
	cmp r5, r4
	movgt r6, #1
	mov r4, r5
	cmp r6, #1
	beq .LU13
	b .LU14
.LU13: 
	mov r5, r4
	movw r0, #12
	bl malloc
	mov r4, r0
	mov r0, r4
	bl free
	sub r4, r5, #1
	mov r6, #0
	mov r5, #0
	cmp r4, r5
	movgt r6, #1
	cmp r6, #1
	beq .LU13
	b .LU14
.LU14: 
	b .LU11
.LU11: 
	pop {r4, r5, r6}
	sub sp, fp, #4
	pop {fp, pc}
	.size objinstantiation, .-objinstantiation
	.align 2
	.global ackermann
ackermann:
.LU16: 
	push {fp, lr}
	add fp, sp, #4
	push {r4, r5, r6, r7}
	mov r7, r0
	mov r6, r1
	mov r5, #0
	mov r4, #0
	cmp r7, r4
	moveq r5, #1
	cmp r5, #1
	beq .LU17
	b .LU18
.LU17: 
	add r4, r6, #1
	b .LU15
.LU18: 
	b .LU19
.LU19: 
	mov r4, #0
	mov r5, #0
	cmp r6, r5
	moveq r4, #1
	cmp r4, #1
	beq .LU20
	b .LU21
.LU20: 
	sub r4, r7, #1
	mov r0, r4
	mov r1, #1
	bl ackermann
	mov r4, r0
	b .LU15
.LU21: 
	sub r5, r7, #1
	sub r4, r6, #1
	mov r0, r7
	mov r1, r4
	bl ackermann
	mov r4, r0
	mov r0, r5
	mov r1, r4
	bl ackermann
	mov r4, r0
	b .LU15
.LU15: 
	mov r0, r4
	pop {r4, r5, r6, r7}
	sub sp, fp, #4
	pop {fp, pc}
	.size ackermann, .-ackermann
	.align 2
	.global main
main:
.LU24: 
	push {fp, lr}
	add fp, sp, #4
	push {r4, r5, r6, r7, r8}
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
	movw r4, #:lower16:.read_scratch
	movt r4, #:upper16:.read_scratch
	ldr r4, [r4]
	movw r1, #:lower16:.read_scratch
	movt r1, #:upper16:.read_scratch
	movw r0, #:lower16:.READ_FMT
	movt r0, #:upper16:.READ_FMT
	bl scanf
	movw r8, #:lower16:.read_scratch
	movt r8, #:upper16:.read_scratch
	ldr r8, [r8]
	movw r1, #:lower16:.read_scratch
	movt r1, #:upper16:.read_scratch
	movw r0, #:lower16:.READ_FMT
	movt r0, #:upper16:.READ_FMT
	bl scanf
	movw r7, #:lower16:.read_scratch
	movt r7, #:upper16:.read_scratch
	ldr r7, [r7]
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
	mov r0, r4
	bl objinstantiation
	mov r1, r4
	movw r0, #:lower16:.PRINTLN_FMT
	movt r0, #:upper16:.PRINTLN_FMT
	bl printf
	mov r0, r8
	mov r1, r7
	bl ackermann
	mov r4, r0
	mov r1, r4
	movw r0, #:lower16:.PRINTLN_FMT
	movt r0, #:upper16:.PRINTLN_FMT
	bl printf
	b .LU23
.LU23: 
	mov r0, #0
	pop {r4, r5, r6, r7, r8}
	sub sp, fp, #4
	pop {fp, pc}
	.size main, .-main
	.section	.rodata
	.align	2
.PRINTLN_FMT:
	.asciz	"%ld\n"
	.align	2
.PRINT_FMT:
	.asciz	"%ld "
	.align	2
.READ_FMT:
	.asciz	"%ld"
	.comm	.read_scratch,4,4
	.global	__aeabi_idiv
