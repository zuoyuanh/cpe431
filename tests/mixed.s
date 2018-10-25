	.file	"mixed.ll"
	.text
	.globl	tailrecursive
	.align	16, 0x90
	.type	tailrecursive,@function
tailrecursive:                          # @tailrecursive
	.cfi_startproc
# BB#0:                                 # %LU1
	pushl	%esi
.Ltmp2:
	.cfi_def_cfa_offset 8
	pushl	%eax
.Ltmp3:
	.cfi_def_cfa_offset 12
.Ltmp4:
	.cfi_offset %esi, -8
	movl	12(%esp), %esi
	testl	%esi, %esi
	jle	.LBB0_2
# BB#1:                                 # %LU4
	movl	$24, (%esp)
	calll	malloc
	decl	%esi
	movl	%esi, (%esp)
	calll	tailrecursive
.LBB0_2:                                # %LU0
	addl	$4, %esp
	popl	%esi
	ret
.Ltmp5:
	.size	tailrecursive, .Ltmp5-tailrecursive
	.cfi_endproc

	.globl	add
	.align	16, 0x90
	.type	add,@function
add:                                    # @add
	.cfi_startproc
# BB#0:                                 # %LU5
	movl	4(%esp), %eax
	addl	8(%esp), %eax
	ret
.Ltmp6:
	.size	add, .Ltmp6-add
	.cfi_endproc

	.globl	domath
	.align	16, 0x90
	.type	domath,@function
domath:                                 # @domath
	.cfi_startproc
# BB#0:                                 # %LU8
	pushl	%ebx
.Ltmp11:
	.cfi_def_cfa_offset 8
	pushl	%edi
.Ltmp12:
	.cfi_def_cfa_offset 12
	pushl	%esi
.Ltmp13:
	.cfi_def_cfa_offset 16
	subl	$8, %esp
.Ltmp14:
	.cfi_def_cfa_offset 24
.Ltmp15:
	.cfi_offset %esi, -16
.Ltmp16:
	.cfi_offset %edi, -12
.Ltmp17:
	.cfi_offset %ebx, -8
	movl	24(%esp), %ebx
	movl	$24, (%esp)
	calll	malloc
	movl	%eax, %esi
	movl	$8, (%esp)
	calll	malloc
	movl	%eax, 8(%esi)
	movl	$24, (%esp)
	calll	malloc
	movl	%eax, %edi
	movl	$8, (%esp)
	calll	malloc
	movl	%eax, 8(%edi)
	movl	%ebx, (%esi)
	movl	$3, (%edi)
	movl	(%esi), %eax
	movl	8(%esi), %ecx
	movl	%eax, (%ecx)
	movl	(%edi), %eax
	movl	8(%edi), %ecx
	movl	%eax, (%ecx)
	jmp	.LBB2_2
	.align	16, 0x90
.LBB2_1:                                # %LU9
                                        #   in Loop: Header=BB2_2 Depth=1
	movl	(%esi), %eax
	movl	8(%edi), %ecx
	movl	(%ecx), %ecx
	movl	%eax, 4(%esp)
	movl	%ecx, (%esp)
	calll	add
	decl	%ebx
.LBB2_2:                                # %LU9
                                        # =>This Inner Loop Header: Depth=1
	testl	%ebx, %ebx
	jg	.LBB2_1
# BB#3:                                 # %LU7
	movl	%esi, (%esp)
	calll	free
	movl	%edi, (%esp)
	calll	free
	addl	$8, %esp
	popl	%esi
	popl	%edi
	popl	%ebx
	ret
.Ltmp18:
	.size	domath, .Ltmp18-domath
	.cfi_endproc

	.globl	objinstantiation
	.align	16, 0x90
	.type	objinstantiation,@function
objinstantiation:                       # @objinstantiation
	.cfi_startproc
# BB#0:                                 # %LU12
	pushl	%esi
.Ltmp21:
	.cfi_def_cfa_offset 8
	pushl	%eax
.Ltmp22:
	.cfi_def_cfa_offset 12
.Ltmp23:
	.cfi_offset %esi, -8
	movl	12(%esp), %esi
	jmp	.LBB3_2
	.align	16, 0x90
.LBB3_1:                                # %LU13
                                        #   in Loop: Header=BB3_2 Depth=1
	movl	$24, (%esp)
	calll	malloc
	movl	%eax, (%esp)
	calll	free
	decl	%esi
.LBB3_2:                                # %LU13
                                        # =>This Inner Loop Header: Depth=1
	testl	%esi, %esi
	jg	.LBB3_1
# BB#3:                                 # %LU11
	addl	$4, %esp
	popl	%esi
	ret
.Ltmp24:
	.size	objinstantiation, .Ltmp24-objinstantiation
	.cfi_endproc

	.globl	ackermann
	.align	16, 0x90
	.type	ackermann,@function
ackermann:                              # @ackermann
	.cfi_startproc
# BB#0:                                 # %LU16
	pushl	%esi
.Ltmp27:
	.cfi_def_cfa_offset 8
	subl	$8, %esp
.Ltmp28:
	.cfi_def_cfa_offset 16
.Ltmp29:
	.cfi_offset %esi, -8
	movl	20(%esp), %eax
	movl	16(%esp), %esi
	testl	%esi, %esi
	je	.LBB4_1
# BB#2:                                 # %LU19
	testl	%eax, %eax
	je	.LBB4_3
# BB#4:                                 # %LU21
	decl	%eax
	movl	%eax, 4(%esp)
	movl	%esi, (%esp)
	decl	%esi
	calll	ackermann
	movl	%eax, 4(%esp)
	movl	%esi, (%esp)
	jmp	.LBB4_5
.LBB4_1:                                # %LU17
	incl	%eax
	jmp	.LBB4_6
.LBB4_3:                                # %LU20
	decl	%esi
	movl	%esi, (%esp)
	movl	$1, 4(%esp)
.LBB4_5:                                # %LU15
	calll	ackermann
.LBB4_6:                                # %LU15
	addl	$8, %esp
	popl	%esi
	ret
.Ltmp30:
	.size	ackermann, .Ltmp30-ackermann
	.cfi_endproc

	.globl	main
	.align	16, 0x90
	.type	main,@function
main:                                   # @main
	.cfi_startproc
# BB#0:                                 # %LU23
	pushl	%ebp
.Ltmp36:
	.cfi_def_cfa_offset 8
	pushl	%ebx
.Ltmp37:
	.cfi_def_cfa_offset 12
	pushl	%edi
.Ltmp38:
	.cfi_def_cfa_offset 16
	pushl	%esi
.Ltmp39:
	.cfi_def_cfa_offset 20
	subl	$12, %esp
.Ltmp40:
	.cfi_def_cfa_offset 32
.Ltmp41:
	.cfi_offset %esi, -20
.Ltmp42:
	.cfi_offset %edi, -16
.Ltmp43:
	.cfi_offset %ebx, -12
.Ltmp44:
	.cfi_offset %ebp, -8
	movl	$.read_scratch, 4(%esp)
	movl	$.L.read, (%esp)
	calll	scanf
	movl	.read_scratch, %esi
	movl	$.read_scratch, 4(%esp)
	movl	$.L.read, (%esp)
	calll	scanf
	movl	.read_scratch, %edi
	movl	$.read_scratch, 4(%esp)
	movl	$.L.read, (%esp)
	calll	scanf
	movl	.read_scratch, %ebx
	movl	$.read_scratch, 4(%esp)
	movl	$.L.read, (%esp)
	calll	scanf
	movl	.read_scratch, %eax
	movl	%eax, 8(%esp)           # 4-byte Spill
	movl	$.read_scratch, 4(%esp)
	movl	$.L.read, (%esp)
	calll	scanf
	movl	.read_scratch, %ebp
	movl	%esi, (%esp)
	calll	tailrecursive
	movl	%esi, 4(%esp)
	movl	$.L.println, (%esp)
	calll	printf
	movl	%edi, (%esp)
	calll	domath
	movl	%edi, 4(%esp)
	movl	$.L.println, (%esp)
	calll	printf
	movl	%ebx, (%esp)
	calll	objinstantiation
	movl	%ebx, 4(%esp)
	movl	$.L.println, (%esp)
	calll	printf
	movl	%ebp, 4(%esp)
	movl	8(%esp), %eax           # 4-byte Reload
	movl	%eax, (%esp)
	calll	ackermann
	movl	%eax, 4(%esp)
	movl	$.L.println, (%esp)
	calll	printf
	xorl	%eax, %eax
	addl	$12, %esp
	popl	%esi
	popl	%edi
	popl	%ebx
	popl	%ebp
	ret
.Ltmp45:
	.size	main, .Ltmp45-main
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
