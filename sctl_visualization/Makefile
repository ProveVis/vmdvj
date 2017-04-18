win:
	ocamlc -c term.ml
	ocamlc -c formula.ml
	ocamlc -c modul.ml
	ocamlc -c prover.ml
	ocamlc -thread -c unix.cma threads.cma prover_output.ml
	ocamllex lexer.mll       
	ocamlyacc parser.mly   
	ocamlc -c parser.mli
	ocamlc -c lexer.ml
	ocamlc -c parser.ml
	ocamlc -c main.ml
	ocamlc -thread -o sctl.exe unix.cma threads.cma term.cmo formula.cmo modul.cmo prover.cmo prover_output.cmo lexer.cmo parser.cmo main.cmo

linux:
	ocamlc -c term.ml
	ocamlc -c formula.ml
	ocamlc -c modul.ml
	ocamlc -c prover.ml
	ocamlc -thread -c unix.cma threads.cma prover_output.ml
	ocamllex lexer.mll       
	ocamlyacc parser.mly   
	ocamlc -c parser.mli
	ocamlc -c lexer.ml
	ocamlc -c parser.ml
	ocamlc -c main.ml
	ocamlc -thread -o sctl unix.cma threads.cma term.cmo formula.cmo modul.cmo prover.cmo prover_output.cmo lexer.cmo parser.cmo main.cmo

clean:
	rm -f *.cmo
	rm -f *.cmi
	rm -f *.o
	rm -f *.mli
	rm -f *.bak
	rm -f *.ml~
	rm -f lexer.ml parser.mli parser.ml
	rm -f sctl.exe
	rm -f sctl