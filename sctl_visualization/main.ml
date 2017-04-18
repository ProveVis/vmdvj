
open Term 
open Formula
open Modul
open Prover_output
open Prover
open Parser

let input_paras pl = 
	let l = List.length pl in
	let para_tbl = Hashtbl.create l in 
	let rec get_para_from_stdin i paras = 
		match paras with
		| (v, vt) :: paras' -> (match vt with
								| Int_type (i1, i2) -> Hashtbl.add para_tbl v (Const (int_of_string Sys.argv.(i)))
								| Bool_type -> Hashtbl.add para_tbl v (Const (let b = Sys.argv.(i) in (if b="true" then 1 else (if b="false" then (-1) else 0))))
								| _ -> print_endline ("invalid input for parameter: "^v^"."); exit 1); get_para_from_stdin (i+1) paras'
		| [] -> () in
	get_para_from_stdin 2 pl; para_tbl

(*let main () = 
	let arg_length = Array.length Sys.argv in
	if arg_length = 2 then 
	(
		let (modl_tbl, modl) = Parser.input Lexer.token (Lexing.from_channel (open_in Sys.argv.(1))) in
		let para_tbl = input_paras modl.parameter_list in
		let m = build_model modl modl_tbl para_tbl in
		print_endline ("verifying on the model "^(m.model_name)^"...");
		Prover.Seq_Prover.prove_model m 
	) else 
	(
		let (modl_tbl, modl) = Parser.input Lexer.token (Lexing.from_channel (open_in Sys.argv.(3))) in
		let para_tbl = input_paras modl.parameter_list in
		let m = build_model modl modl_tbl para_tbl in
		let out = open_out Sys.argv.(2) in
		print_endline ("verifying on the model "^(m.model_name)^"...");
		Prover_output.prove_model m out Sys.argv.(2);
		close_out out
	)*)

let choose_to_prove output_file ipaddr input = 
	match output_file with
	| None -> 
		let modl_tbl, modl = Parser.input Lexer.token (Lexing.from_channel (open_in input)) in
		let para_tbl = input_paras modl.parameter_list in
		let m = build_model modl modl_tbl para_tbl in
		print_endline ("verifying on the model "^(m.model_name)^"...");
		Prover.Seq_Prover.prove_model m 
	| Some oname ->
		let (modl_tbl, modl) = Parser.input Lexer.token (Lexing.from_channel (open_in input)) in
		let para_tbl = input_paras modl.parameter_list in
		let m = build_model modl modl_tbl para_tbl in
		let out = open_out oname in
		print_endline ("verifying on the model "^(m.model_name)^"...");
		Prover_output.prove_model m out oname ipaddr;
		close_out out

let main () =
	let output_file = ref None
	and ipaddr = ref "127.0.0.1" in
	Arg.parse
		[
			"-output", Arg.String (fun s -> output_file := Some s), " The output file";
			"-ipaddr", Arg.String (fun s -> ipaddr := s), " The IP address of the visualization server";
		]
		(fun s -> choose_to_prove !output_file !ipaddr s)
		"Usage: sctl [-ouput <filename>] [-ipaddr <IP_Address>] <filename>"

let _ = 
	Printexc.print main ()
