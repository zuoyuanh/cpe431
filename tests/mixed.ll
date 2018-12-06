	.arch armv7-a
	.comm   globalfoo,4,4

	.text
	.align 2
	.global tailrecursive
tailrecursive:
.LU1: 
	push {fp, lr}
	add fp, sp, #4
	mov r1, r1
	mov r2, #0
	mov r0, #0
	cmp r1, r0
	movle r2, #1
	cmp r2, #1
	beq .LU2
	b .LU3
.LU2: 
	b .LU0
.LU3: 
	b .LU4
.LU4: 
	sub SPILL(u3), r1, #1
	mov r0, SPILL(u3)
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
	mov r0, r1
	mov r0, r0
	add r2, r0, r0
	b .LU5
.LU5: 
	mov r0, r2
	pop {fp, pc}
	.size add, .-add
	.align 2
	.global domath
domath:
.LU8: 
	push {fp, lr}
	add fp, sp, #4
	mov SPILL(%num), SPILL(r0)
	mov r5, SPILL(%num)
	movw r0, #12
	bl malloc
	mov SPILL(u8), r0
	mov SPILL(u9), SPILL(u8)
	mov r1, SPILL(u9)
	mov r2, SPILL(u9)
	add r7, SPILL(u9), #8
	movw r0, #4
	bl malloc
	mov r7, r0
	mov r7, r7
	str r7, [r7]
	movw r0, #12
	bl malloc
	mov SPILL(u13), r0
	mov SPILL(u14), SPILL(u13)
	mov r4, SPILL(u14)
	mov r3, SPILL(u14)
	add r0, SPILL(u14), #8
	movw r0, #4
	bl malloc
	mov r0, r0
	mov r0, r0
	str r0, [r0]
	add r6, SPILL(u9), #0
	str SPILL(%num), [r6]
	add r5, SPILL(u14), #0
	mov r5, #3
	str r5, [r5]
	add r4, SPILL(u9), #8
	ldr r4, [r4]
	add r2, r4, #0
	add r2, SPILL(u9), #0
	ldr r2, [r2]
	str r2, [r2]
	add r3, SPILL(u14), #8
	ldr r3, [r3]
	add r1, r3, #0
	add r1, SPILL(u14), #0
	ldr r1, [r1]
	str r1, [r1]
	mov r8, #0
	mov SPILL(u69), #0
	cmp SPILL(%num), SPILL(u69)
	movgt r8, #1
	cmp r8, #1
	beq .LU9
	b .LU10
.LU9: 
	mov r0, r5
	mov r8, r3
	mov r4, r8
	mov r3, r8
	mov r7, r2
	mov r1, r7
	mov r2, r7
	add r6, r8, #8
	ldr r4, [r6]
	add r4, r4, #0
	ldr r1, [r4]
	add r2, r7, #0
	ldr r1, [r2]
	mov r1, r1
	mov r0, r1
	bl add
	mov r0, r0
	sub r0, r0, #1
	mov r5, r0
	mov r5, #0
	mov r3, #0
	cmp r0, r3
	movgt r5, #1
	cmp r5, #1
	beq .LU9
	b .LU10
.LU10: 
	mov SPILL(u62), r4
	mov r0, r1
	mov r0, r0
	bl free
	mov r0, SPILL(u62)
	bl free
	b .LU7
.LU7: 
	pop {fp, pc}
	.size domath, .-domath
	.align 2
	.global objinstantiation
objinstantiation:
.LU12: 
	push {fp, lr}
	add fp, sp, #4
	mov r2, r2
	mov r1, r2
	mov r1, #0
	mov r0, #0
	cmp r2, r0
	movgt r1, #1
	cmp r1, #1
	beq .LU13
	b .LU14
.LU13: 
	mov r0, r1
	movw r0, #12
	bl malloc
	mov r2, r0
	mov r2, r2
	mov r0, r2
	bl free
	sub r0, r0, #1
	mov r1, r0
	mov r1, #0
	mov r3, #0
	cmp r0, r3
	movgt r1, #1
	cmp r1, #1
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
	mov r5, r0
	mov r2, r0
	mov r2, #0
	mov r1, #0
	cmp r5, r1
	moveq r2, #1
	cmp r2, #1
	beq .LU17
	b .LU18
.LU17: 
	add r0, r2, #1
	mov r4, r0
	b .LU15
.LU18: 
	b .LU19
.LU19: 
	mov r0, #0
	mov r1, #0
	cmp r2, r1
	moveq r0, #1
	cmp r0, #1
	beq .LU20
	b .LU21
.LU20: 
	sub r0, r5, #1
	mov r1, #1
	mov r0, r0
	bl ackermann
	mov r1, r0
	mov r4, r1
	b .LU15
.LU21: 
	sub r0, r5, #1
	sub r2, r2, #1
	mov r1, r2
	mov r0, r5
	bl ackermann
	mov r3, r0
	mov r1, r3
	mov r0, r0
	bl ackermann
	mov r1, r0
	mov r4, r1
	b .LU15
.LU15: 
	mov SPILL(u89), r4
	mov r0, SPILL(u89)
	pop {fp, pc}
	.size ackermann, .-ackermann
	.align 2
	.global main
main:
.LU24: 
	push {fp, lr}
	add fp, sp, #4
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
	mov r0, r0
	mov r1, r0
	movw r0, #:lower16:.PRINTLN_FMT
	movt r0, #:upper16:.PRINTLN_FMT
	bl printf
	b .LU23
.LU23: 
	mov r0, #0
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
