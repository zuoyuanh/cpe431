	.file	"mixed_ans.ll"
	.text
	.globl	tailrecursive
	.align	16, 0x90
	.type	tailrecursive,@function
tailrecursive:                          # @tailrecursive
	.cfi_startproc
# BB#0:                                 # %LU1
	subl	$12, %esp
.Ltmp1:
	.cfi_def_cfa_offset 16
	movl	16(%esp), %eax
	movl	%eax, 8(%esp)
	testl	%eax, %eax
	jle	.LBB0_2
# BB#1:                                 # %LU6
	movl	$24, (%esp)
	calll	malloc
	movl	%eax, 4(%esp)
	movl	8(%esp), %eax
	decl	%eax
	movl	%eax, (%esp)
	calll	tailrecursive
.LBB0_2:                                # %LU0
	addl	$12, %esp
	ret
.Ltmp2:
	.size	tailrecursive, .Ltmp2-tailrecursive
	.cfi_endproc

	.globl	add
	.align	16, 0x90
	.type	add,@function
add:                                    # @add
	.cfi_startproc
# BB#0:                                 # %LU10
	subl	$12, %esp
.Ltmp4:
	.cfi_def_cfa_offset 16
	movl	20(%esp), %eax
	movl	16(%esp), %ecx
	movl	%ecx, 4(%esp)
	movl	%eax, (%esp)
	addl	4(%esp), %eax
	movl	%eax, 8(%esp)
	movl	8(%esp), %eax
	addl	$12, %esp
	ret
.Ltmp5:
	.size	add, .Ltmp5-add
	.cfi_endproc

	.globl	domath
	.align	16, 0x90
	.type	domath,@function
domath:                                 # @domath
	.cfi_startproc
# BB#0:                                 # %LU13
	subl	$24, %esp
.Ltmp7:
	.cfi_def_cfa_offset 28
	movl	28(%esp), %eax
	movl	%eax, 20(%esp)
	movl	$24, (%esp)
	calll	malloc
	movl	%eax, 16(%esp)
	movl	$8, (%esp)
	calll	malloc
	movl	16(%esp), %ecx
	movl	%eax, 8(%ecx)
	movl	$24, (%esp)
	calll	malloc
	movl	%eax, 8(%esp)
	movl	$8, (%esp)
	calll	malloc
	movl	8(%esp), %ecx
	movl	%eax, 8(%ecx)
	movl	20(%esp), %eax
	movl	16(%esp), %ecx
	movl	%eax, (%ecx)
	movl	8(%esp), %eax
	movl	$3, (%eax)
	movl	16(%esp), %eax
	movl	(%eax), %ecx
	movl	8(%eax), %eax
	movl	%ecx, (%eax)
	movl	8(%esp), %eax
	movl	(%eax), %ecx
	movl	8(%eax), %eax
	movl	%ecx, (%eax)
	cmpl	$0, 20(%esp)
	jle	.LBB2_2
	.align	16, 0x90
.LBB2_1:                                # %LU24
                                        # =>This Inner Loop Header: Depth=1
	movl	16(%esp), %eax
	movl	(%eax), %eax
	movl	8(%esp), %ecx
	imull	(%ecx), %eax
	movl	%eax, 12(%esp)
	movl	16(%esp), %ecx
	movl	8(%ecx), %ecx
	imull	(%ecx), %eax
	movl	8(%esp), %ecx
	cltd
	idivl	(%ecx)
	movl	%eax, 12(%esp)
	movl	8(%esp), %eax
	movl	8(%eax), %eax
	movl	(%eax), %eax
	movl	16(%esp), %ecx
	movl	(%ecx), %ecx
	movl	%ecx, 4(%esp)
	movl	%eax, (%esp)
	calll	add
	movl	%eax, 12(%esp)
	movl	8(%esp), %eax
	movl	(%eax), %eax
	movl	16(%esp), %ecx
	subl	(%ecx), %eax
	movl	%eax, 12(%esp)
	movl	20(%esp), %eax
	decl	%eax
	movl	%eax, 20(%esp)
	testl	%eax, %eax
	jg	.LBB2_1
.LBB2_2:                                # %LU12
	movl	16(%esp), %eax
	movl	%eax, (%esp)
	calll	free
	movl	8(%esp), %eax
	movl	%eax, (%esp)
	calll	free
	addl	$24, %esp
	ret
.Ltmp8:
	.size	domath, .Ltmp8-domath
	.cfi_endproc

	.globl	objinstantiation
	.align	16, 0x90
	.type	objinstantiation,@function
objinstantiation:                       # @objinstantiation
	.cfi_startproc
# BB#0:                                 # %LU33
	subl	$12, %esp
.Ltmp10:
	.cfi_def_cfa_offset 16
	movl	16(%esp), %eax
	jmp	.LBB3_2
	.align	16, 0x90
.LBB3_1:                                # %LU36
                                        #   in Loop: Header=BB3_2 Depth=1
	movl	$24, (%esp)
	calll	malloc
	movl	%eax, 4(%esp)
	movl	%eax, (%esp)
	calll	free
	movl	8(%esp), %eax
	decl	%eax
.LBB3_2:                                # %LU36
                                        # =>This Inner Loop Header: Depth=1
	movl	%eax, 8(%esp)
	testl	%eax, %eax
	jg	.LBB3_1
# BB#3:                                 # %LU32
	addl	$12, %esp
	ret
.Ltmp11:
	.size	objinstantiation, .Ltmp11-objinstantiation
	.cfi_endproc

	.globl	ackermann
	.align	16, 0x90
	.type	ackermann,@function
ackermann:                              # @ackermann
	.cfi_startproc
# BB#0:                                 # %LU41
	pushl	%esi
.Ltmp14:
	.cfi_def_cfa_offset 8
	subl	$20, %esp
.Ltmp15:
	.cfi_def_cfa_offset 28
.Ltmp16:
	.cfi_offset %esi, -8
	movl	32(%esp), %eax
	movl	28(%esp), %ecx
	movl	%ecx, 12(%esp)
	movl	%eax, 8(%esp)
	cmpl	$0, 12(%esp)
	je	.LBB4_1
# BB#2:                                 # %LU46
	cmpl	$0, 8(%esp)
	je	.LBB4_3
# BB#4:                                 # %LU50
	movl	12(%esp), %esi
	movl	8(%esp), %eax
	decl	%eax
	movl	%eax, 4(%esp)
	movl	%esi, (%esp)
	decl	%esi
	calll	ackermann
	movl	%eax, 4(%esp)
	movl	%esi, (%esp)
	jmp	.LBB4_5
.LBB4_1:                                # %LU43
	movl	8(%esp), %eax
	incl	%eax
	jmp	.LBB4_6
.LBB4_3:                                # %LU48
	movl	12(%esp), %eax
	decl	%eax
	movl	%eax, (%esp)
	movl	$1, 4(%esp)
.LBB4_5:                                # %LU40
	calll	ackermann
.LBB4_6:                                # %LU40
	movl	%eax, 16(%esp)
	movl	16(%esp), %eax
	addl	$20, %esp
	popl	%esi
	ret
.Ltmp17:
	.size	ackermann, .Ltmp17-ackermann
	.cfi_endproc

	.globl	main
	.align	16, 0x90
	.type	main,@function
main:                                   # @main
	.cfi_startproc
# BB#0:                                 # %LU53
	subl	$32, %esp
.Ltmp19:
	.cfi_def_cfa_offset 36
	leal	24(%esp), %eax
	movl	%eax, 4(%esp)
	movl	$.L.read, (%esp)
	calll	scanf
	leal	20(%esp), %eax
	movl	%eax, 4(%esp)
	movl	$.L.read, (%esp)
	calll	scanf
	leal	16(%esp), %eax
	movl	%eax, 4(%esp)
	movl	$.L.read, (%esp)
	calll	scanf
	leal	12(%esp), %eax
	movl	%eax, 4(%esp)
	movl	$.L.read, (%esp)
	calll	scanf
	leal	8(%esp), %eax
	movl	%eax, 4(%esp)
	movl	$.L.read, (%esp)
	calll	scanf
	movl	24(%esp), %eax
	movl	%eax, (%esp)
	calll	tailrecursive
	movl	24(%esp), %eax
	movl	%eax, 4(%esp)
	movl	$.L.println, (%esp)
	calll	printf
	movl	20(%esp), %eax
	movl	%eax, (%esp)
	calll	domath
	movl	20(%esp), %eax
	movl	%eax, 4(%esp)
	movl	$.L.println, (%esp)
	calll	printf
	movl	16(%esp), %eax
	movl	%eax, (%esp)
	calll	objinstantiation
	movl	16(%esp), %eax
	movl	%eax, 4(%esp)
	movl	$.L.println, (%esp)
	calll	printf
	movl	12(%esp), %eax
	movl	8(%esp), %ecx
	movl	%ecx, 4(%esp)
	movl	%eax, (%esp)
	calll	ackermann
	movl	%eax, 4(%esp)
	movl	$.L.println, (%esp)
	calll	printf
	movl	$0, 28(%esp)
	movl	28(%esp), %eax
	addl	$32, %esp
	ret
.Ltmp20:
	.size	main, .Ltmp20-main
	.cfi_endproc

	.type	globalfoo,@object       # @globalfoo
	.comm	globalfoo,4,8
	.type	.L.println,@object      # @.println
	.section	.rodata.str1.1,"aMS",@progbits,1
.L.println:
	.asciz	"%ld\n"
	.size	.L.println, 5

	.type	.L.print,@object        # @.print
.L.print:
	.asciz	"%ld "
	.size	.L.print, 5

	.type	.L.read,@object         # @.read
.L.read:
	.asciz	"%ld"
	.size	.L.read, 4

	.type	.read_scratch,@object   # @.read_scratch
	.comm	.read_scratch,4,8

	.section	".note.GNU-stack","",@progbits
