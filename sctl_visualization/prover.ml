open Term
open Formula
open Modul

module type Prover =
	sig
		type continuation = 
		| Basic of bool
		| Cont of State_set.t * formula * string * continuation * continuation

		type fml_state_tbl = (string, State_set.t) Hashtbl.t

		exception Error_proving_atomic
		exception Unable_to_prove
		
		val merges : fml_state_tbl
		val pre_process_merges : (string, formula) Hashtbl.t -> unit
		val post_process_merges : unit -> unit
		
		val state_in_merge : fml_state_tbl -> string -> (int array) -> bool
		val add_merge : fml_state_tbl -> string -> State_set.t -> unit

		val prove : continuation -> (Modul.model) -> bool
		val prove_model : Modul.model -> unit
		val make_ax_cont : State_set.t -> state -> formula -> string -> State_set.t -> continuation -> continuation -> continuation
		val make_ex_cont : State_set.t -> state -> formula -> string -> State_set.t -> continuation -> continuation -> continuation
		val make_af_cont : State_set.t -> state -> formula -> string -> State_set.t -> continuation -> continuation -> continuation
		val make_eg_cont : State_set.t -> state -> formula -> string -> State_set.t -> continuation -> continuation -> continuation
		val make_ar_cont : State_set.t -> state -> state -> formula -> formula -> string -> State_set.t -> continuation -> continuation -> continuation
		val make_eu_cont : State_set.t -> state -> state -> formula -> formula -> string -> State_set.t -> continuation -> continuation -> continuation
	end

module Seq_Prover : Prover = 
	struct
	type continuation = 
		| Basic of bool
		| Cont of State_set.t * formula * string * continuation * continuation

	type fml_state_tbl = (string, State_set.t) Hashtbl.t

	exception Error_proving_atomic
	exception Unable_to_prove

	(*whether state s is already in an existing merge*)
	let merges = Hashtbl.create 10
	let pre_process_merges sub_fml_tbl = 
		Hashtbl.iter (fun a b -> Hashtbl.add merges a State_set.empty) sub_fml_tbl
	let post_process_merges () = 
		Hashtbl.iter (fun a b -> print_endline (a ^ ": " ^ (string_of_int (State_set.cardinal b)))) merges

	let state_in_merge merg fml st = 
		let sts = Hashtbl.find merg fml in State_set.mem st sts
	let add_merge merg fml sts = 
		let sts' = Hashtbl.find merg fml in
		Hashtbl.replace merg fml (State_set.union sts sts')

	(* produce new continuations *)
	let rec make_ax_cont gamma s fml levl sl contl contr = 
		State_set.fold (fun a c -> Cont (gamma, Formula.subst_s fml s (State a), levl, c, contr)) sl contl
	let rec make_ex_cont gamma s fml levl sl contl contr =
		State_set.fold (fun a c -> Cont (gamma, Formula.subst_s fml s (State a), levl, contl, c)) sl contr
	let rec make_af_cont gamma s fml levl sl contl contr =
		State_set.fold (fun a c -> Cont (gamma, AF (s, fml, (State a)), levl, c, contr)) sl contl
	let rec make_eg_cont gamma s fml levl sl contl contr =
		State_set.fold (fun a c -> Cont (gamma, EG (s, fml, (State a)), levl, contl, c)) sl contr
	let rec make_ar_cont gamma s s' fml1 fml2 levl sl contl contr =
		State_set.fold (fun a c -> Cont (gamma, AR (s, s', fml1, fml2, (State a)), levl, c, contr)) sl contl
	let rec make_eu_cont gamma s s' fml1 fml2 levl sl contl contr =
		State_set.fold (fun a c -> Cont (gamma, EU (s, s', fml1, fml2, (State a)), levl, contl, c)) sl contr

	(* proving function, two parameters, first a continuation, second a atomic formula context *)
	let rec prove cont modl = 
		match cont with
		| Basic b -> b
		| Cont (gamma, fml, levl, contl, contr) -> (*(print_endline (Formula.to_string fml));*)(
			match fml with
			| Top -> prove contl modl
			| Bottom -> prove contr modl
			| Atomic (s, sl) -> let v = apply_atomic (Hashtbl.find modl.model_atomic_tbl s) sl in
									if v = Top then (prove contl modl) else if v = Bottom then prove contr modl else raise Error_proving_atomic
			| Neg Atomic (s, sl) -> let v = apply_atomic (Hashtbl.find modl.model_atomic_tbl s) sl in
										if v = Top then prove contr modl else if v = Bottom then prove contl modl else raise Error_proving_atomic
			| And (fml1, fml2) -> prove (Cont (State_set.empty, fml1, (levl^"1"), Cont (State_set.empty, fml2, (levl^"2"), contl, contr), contr)) modl
			| Or (fml1, fml2) -> prove (Cont (State_set.empty, fml1, (levl^"1"), contl, Cont (State_set.empty, fml2, (levl^"2"), contl, contr))) modl
			| AX (s, fml1, State sa) -> prove (make_ax_cont State_set.empty s fml1 (levl^"1") ((next sa modl.model_transitions)) contl contr) modl
			| EX (s, fml1, State sa) -> prove (make_ex_cont State_set.empty s fml1 (levl^"1") ((next sa modl.model_transitions)) contl contr) modl
			| AF (s, fml1, State sa) -> if State_set.mem sa gamma then 
									(prove contr modl) else 
									(
									prove (Cont (State_set.empty, Formula.subst_s fml1 s (State sa), (levl^"1"), contl, make_af_cont (State_set.add sa gamma) s fml1 levl (((next sa modl.model_transitions))) contl contr)) modl)
			(*| AF (s, fml1, State sa) -> if State_set.mem sa gamma then 
									(add_merge merges levl gamma; prove contr modl) else 
									(if state_in_merge merges levl sa then prove contr modl else 
									prove (Cont (State_set.empty, Formula.subst_s fml1 s (State sa), (levl^"1"), contl, make_af_cont (State_set.add sa gamma) s fml1 levl (((next sa modl.model_transitions))) contl contr)) modl) *)
			| EG (s, fml1, State sa) -> if State_set.mem sa gamma then 
									(prove contl modl) else
									( 
									prove (Cont (State_set.empty, Formula.subst_s fml1 s (State sa), (levl^"1"), make_eg_cont (State_set.add sa gamma) s fml1 levl (((next sa modl.model_transitions))) contl contr, contr)) modl)
			(*| EG (s, fml1, State sa) -> if State_set.mem sa gamma then 
									(add_merge merges levl gamma; prove contl modl) else
									(if state_in_merge merges levl sa then prove contl modl else 
									prove (Cont (State_set.empty, Formula.subst_s fml1 s (State sa), (levl^"1"), make_eg_cont (State_set.add sa gamma) s fml1 levl (((next sa modl.model_transitions))) contl contr, contr)) modl)*)
			| AR(x, y, fml1, fml2, State sa) -> if (State_set.is_empty gamma) then Hashtbl.replace merges levl State_set.empty else add_merge merges levl gamma; if State_set.mem sa gamma then 
											(prove contl modl) else 
											(if state_in_merge merges levl sa then prove contl modl else
				prove (Cont (State_set.empty, Formula.subst_s fml2 y (State sa), (levl^"2"), Cont (State_set.empty, Formula.subst_s fml1 x (State sa), (levl^"1"), contl, make_ar_cont (State_set.singleton sa) x y fml1 fml2 levl (((next sa modl.model_transitions))) contl contr), contr)) modl)
			(*| AR(x, y, fml1, fml2, State sa) -> if (State_set.is_empty gamma) then Hashtbl.replace merges levl State_set.empty; if State_set.mem sa gamma then 
											(add_merge merges levl gamma; prove contl modl) else 
											(if state_in_merge merges levl sa then prove contl modl else
				prove (Cont (State_set.empty, Formula.subst_s fml2 y (State sa), (levl^"2"), Cont (State_set.empty, Formula.subst_s fml1 x (State sa), (levl^"1"), contl, make_ar_cont (State_set.add sa gamma) x y fml1 fml2 levl (((next sa modl.model_transitions))) contl contr), contr)) modl)*)
			
			| EU (s, s', fml1, fml2, State sa) -> if (State_set.is_empty gamma) then Hashtbl.replace merges levl State_set.empty else add_merge merges levl gamma; if State_set.mem sa gamma then 
												(prove contr modl) else
												( if state_in_merge merges levl sa then prove contr modl else
									((prove (Cont (State_set.empty, Formula.subst_s fml2 s' (State sa), (levl^"2"), contl, Cont (State_set.empty, Formula.subst_s fml1 s (State sa), (levl^"1"), make_eu_cont (State_set.singleton sa) s s' fml1 fml2 levl (((next sa modl.model_transitions))) contl contr, contr))) modl)))
			(*| EU (s, s', fml1, fml2, State sa) -> if (State_set.is_empty gamma) then Hashtbl.replace merges levl State_set.empty; if State_set.mem sa gamma then 
												(add_merge merges levl gamma; prove contr modl) else
												( if state_in_merge merges levl sa then prove contr modl else
									((prove (Cont (State_set.empty, Formula.subst_s fml2 s' (State sa), (levl^"2"), contl, Cont (State_set.empty, Formula.subst_s fml1 s (State sa), (levl^"1"), make_eu_cont (State_set.add sa gamma) s s' fml1 fml2 levl (((next sa modl.model_transitions))) contl contr, contr))) modl)))  *)
			| _ -> raise Unable_to_prove
			)
	
	let rec prove_model modl = 
		let spec_lst = modl.model_spec_list in 
		let rec prove_lst lst = 
		match lst with
		| [] -> ()
		| (s, fml) :: lst' -> ((let nnf_fml = nnf fml in 
								print_endline (fml_to_string (nnf_fml));
								pre_process_merges (select_sub_fmls (sub_fmls nnf_fml "1"));
								let b = (prove (Cont (State_set.empty, Formula.subst_s (nnf_fml) (SVar "ini") modl.model_init_state, "1", Basic true, Basic false)) modl) in
								 print_endline (s ^ ": " ^ (string_of_bool b)));
								 prove_lst lst') in prove_lst spec_lst

	end







