open Term
open Formula
open Modul

open Unix
open Thread
open Str

	type continuation = 
		| Basic of bool
		| Cont of State_set.t * formula * string * string * (unit -> continuation) * (unit -> continuation)

	type fml_state_tbl = (string, State_set.t) Hashtbl.t

	exception Error_proving_atomic
	exception Unable_to_prove

	(*functions for the output*)
	let sequents = Hashtbl.create 100
	let proof = Hashtbl.create 100
	let counterexample = Hashtbl.create 100

	let output_result b s seqts prof out vt = 
		output_string out (if b then ("proof for "^ s ^":\r\n") else ("counterexample for "^s^":\r\n"));
		let tmp_queue = ref ["0"] in
		let rec str_int_list il = 
			(match il with
			| [] -> ""
			| [i] ->  i
			| i :: il' -> (i)^", "^(str_int_list il')) in
		let rec str_sequent seqt = 
			(let gamma = fst seqt and fml = snd seqt in
			 let str_gamma = (State_set.fold (fun a b -> (str_modl_state vt a)^"\r\n"^b) gamma "") in
				(if str_gamma = "" then "" else str_gamma^"") ^"|- "^(str_modl_fml vt fml)) in
		let rec output_tmp_queue () = 
			if (List.length !tmp_queue > 0) then 
				(let h = List.hd !tmp_queue in 
				(*output_string out ((h)^": "^(str_sequent (try Hashtbl.find sequents h with Not_found -> (print_endline ("not found sequent wit index "^(h)); (exit 0))))^"\t\t"); 	*)
				output_string out ((h)^": "^((try str_sequent (Hashtbl.find sequents h) with Not_found -> (("not found sequent wit index "^(h)))))^"\t\t"); 			
				output_string out ("["^(str_int_list (try Hashtbl.find (prof) h with Not_found -> []))^"]\r\n\r\n"); 
				tmp_queue := (List.tl !tmp_queue) @ (try Hashtbl.find prof h with Not_found -> []); 
				output_tmp_queue ()) in
		output_tmp_queue ()


	let current_id = ref 0
	let new_id () = current_id := !current_id + 1; (string_of_int !current_id)

(**network related**)
let state_index = ref 0
let state_tbl = Hashtbl.create 100
let state_struct_tbl = Hashtbl.create 100

let send_str o str = 
	Printf.fprintf o "%s" (str^"\n"); flush o

let rec receive_loop (i, o) = 
	let s = input_line i in
		print_endline ("got message from server: "^ s);
		let s_prefix = String.sub s 0 11 in
		(if s_prefix = "high_state:" then 
		begin
			let fmlId = String.sub s 11 (String.length s - 12) in
				print_endline ("highlight state from formula: "^ fmlId);
				try
				send_str o ("high_state:"^(state_str_from_fml (snd (Hashtbl.find sequents (fmlId)))))
				with Not_found -> print_endline ("formula not found: "^fmlId^"!")
		end else if s_prefix = "unhi_state:" then
		begin
			let fmlId = String.sub s 11 (String.length s - 12) in
				send_str o ("unhi_state:"^(state_str_from_fml (snd (Hashtbl.find sequents (fmlId)))))
		end else 
			print_endline ("unknown command from the server: "^s)); receive_loop (i, o)

let send_proof o = 
	Hashtbl.iter (fun a b -> send_str o ("proof_node:"^(a)^"-->"^(fml_to_string (snd b)))) sequents;
	let tmp_fmls = ref ["0"] in
	while !tmp_fmls <> [] do
		try
		let tmp_is = Hashtbl.find proof (List.hd !tmp_fmls) in
		List.iter (fun a -> send_str o ("proof_edge:"^((List.hd !tmp_fmls))^"-->"^(a))) tmp_is;
		tmp_fmls := tmp_is @ (List.tl !tmp_fmls) 
		with Not_found -> (tmp_fmls := List.tl !tmp_fmls)
	done

let send_state_graph o = 
	Hashtbl.iter (fun a b -> send_str o ("state_node:"^(b)^"-->"^a)) state_tbl;
	Hashtbl.iter (fun a b -> List.iter (fun c -> send_str o ("state_edge:"^((a))^"-->"^(c))) b) state_struct_tbl
(*
	let tmp_fmls = ref [1] in
	while !tmp_fmls <> [] do
		try
		let tmp_is = Hashtbl.find state_struct_tbl (List.hd !tmp_fmls) in
		List.iter (fun a -> send_str o ("state_edge:"^(string_of_int (List.hd !tmp_fmls))^"->"^(string_of_int a))) tmp_is;
		tmp_fmls := tmp_is @ (List.tl !tmp_fmls) 
		with Not_found -> (tmp_fmls := List.tl !tmp_fmls)
	done
*)

(*******************)

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

	let add_premises prf fid pid = 
		try
			Hashtbl.replace prf fid (pid :: (Hashtbl.find prf fid))
		with Not_found -> Hashtbl.add prf fid [pid]

	(* produce new continuations *)
	let rec make_ax_cont gamma s fml id levl sl contl contr = 
		let rec tmp_ax_cont sts = 
			State_set.fold (fun a b -> let nid = new_id() in (fun () -> Cont (gamma, Formula.subst_s fml s (State a), (nid), levl, (fun () -> add_premises proof id nid; b()), (fun () -> add_premises counterexample id nid; contr ())))) sts contl in
(*			match sts with
			| [] -> contl
			| st :: sts' -> (fun () -> Cont (gamma, Formula.subst_s fml s (State st), (tmp_id := !tmp_id + 1; !tmp_id), levl, (fun () -> add_premises proof id !tmp_id; (tmp_ax_cont sts')()), (fun () -> add_premises counterexample id !tmp_id; contr ()))) in*)
		tmp_ax_cont sl 

	(*	State_set.fold (fun a c -> Cont (gamma, Formula.subst_s fml s (State a), levl, c, contr)) sl contl *)
	let rec make_ex_cont gamma s fml id levl sl contl contr =
		let rec tmp_ex_cont sts = 
			State_set.fold (fun a b -> let nid = new_id() in (fun () -> Cont (gamma, Formula.subst_s fml s (State a), (nid), levl, (fun () -> add_premises proof id nid; contl ()), (fun () -> add_premises counterexample id nid; b())))) sts contr in
(*			match sts with
			| [] -> contr
			| st :: sts' -> (fun () -> Cont (gamma, Formula.subst_s fml s (State st), (tmp_id := !tmp_id + 1; !tmp_id), levl, (fun () -> add_premises proof id !tmp_id; contl ()), (fun () -> add_premises counterexample id !tmp_id; (tmp_ex_cont sts')()))) in *)
		tmp_ex_cont sl

	(*	State_set.fold (fun a c -> Cont (gamma, Formula.subst_s fml s (State a), levl, contl, c)) sl contr *)
	let rec make_af_cont gamma s fml id levl sl contl contr =
		let rec tmp_af_cont sts = 
			State_set.fold (fun a b -> let nid = new_id() in (fun () -> Cont (gamma, AF (s, fml, (State a)), (nid), levl, (fun () -> add_premises proof id nid; b()), (fun () -> add_premises counterexample id nid; contr ())))) sts contl in 
	(*		match sts with
			| [] -> contl
			| st :: sts' -> (fun () -> Cont (gamma, AF (s, fml, (State st)), (tmp_id := !tmp_id + 1; !tmp_id), levl, (fun () -> add_premises proof id !tmp_id; (tmp_af_cont sts')()), (fun () -> add_premises counterexample id !tmp_id; contr ()))) in *)
		tmp_af_cont sl


	(*	State_set.fold (fun a c -> Cont (gamma, AF (s, fml, (State a)), levl, c, contr)) sl contl *)
	let rec make_eg_cont gamma s fml id levl sl contl contr =
		let rec tmp_eg_cont sts = 
			State_set.fold (fun a b -> let nid = new_id() in (fun () -> Cont (gamma, EG (s, fml, (State a)), (nid), levl, (fun () -> add_premises proof id nid; contl ()), (fun () -> add_premises counterexample id nid; b())))) sts contr in
	(*		match sts with
			| [] -> contr
			| st :: sts' -> (fun () -> Cont (gamma, EG (s, fml, (State st)), (tmp_id := !tmp_id + 1; !tmp_id), levl, (fun () -> add_premises proof id !tmp_id; contl ()), (fun () -> add_premises counterexample id !tmp_id; (tmp_eg_cont sts')()))) in *)
		tmp_eg_cont sl

		(*State_set.fold (fun a c -> Cont (gamma, EG (s, fml, (State a)), levl, contl, c)) sl contr *)
	let rec make_ar_cont gamma s s' fml1 fml2 id levl sl contl contr =
		let rec tmp_ar_cont sts = 
			State_set.fold (fun a b -> let nid = new_id() in (fun () -> Cont (gamma, AR (s, s', fml1, fml2, (State a)), (nid), levl, (fun () -> add_premises proof id nid; b()), (fun () -> add_premises counterexample id nid; contr ())))) sts contl in 
	(*		match sts with
			| [] -> contl
			| st :: sts' -> (fun () -> Cont (gamma, AR (s, s', fml1, fml2, (State st)), (tmp_id := !tmp_id + 1; !tmp_id), levl, (fun () -> add_premises proof id !tmp_id; (tmp_ar_cont sts')()), (fun () -> add_premises counterexample id !tmp_id; contr ()))) in *)
		tmp_ar_cont sl


		(*State_set.fold (fun a c -> Cont (gamma, AR (s, s', fml1, fml2, (State a)), levl, c, contr)) sl contl *)
	let rec make_eu_cont gamma s s' fml1 fml2 id levl sl contl contr =
		let rec tmp_eu_cont sts = 
			State_set.fold (fun a b -> let nid = new_id() in (fun () -> Cont (gamma, EU (s, s', fml1, fml2, (State a)), (nid), levl, (fun () -> add_premises proof id nid; contl ()), (fun () -> add_premises counterexample id nid; b())))) sts contr in
		(*	match sts with
			| [] -> contr
			| st :: sts' -> (fun () -> Cont (gamma, EU (s, s', fml1, fml2, (State st)), (tmp_id := !tmp_id + 1; !tmp_id), levl, (fun () -> add_premises proof id !tmp_id; contl ()), (fun () -> add_premises counterexample id !tmp_id; (tmp_eu_cont sts')()))) in *)
		tmp_eu_cont sl

		(*State_set.fold (fun a c -> Cont (gamma, EU (s, s', fml1, fml2, (State a)), levl, contl, c)) sl contr*)

	(* proving function, two parameters, first a continuation, second a atomic formula context *)
	let get_state_id s = 
		let str_from = str_state s in
	try		
		Hashtbl.find state_tbl str_from
	with Not_found -> 
		begin
			incr state_index;
			Hashtbl.add state_tbl str_from (string_of_int !state_index);
			(string_of_int !state_index)
		end	


	let add_states sf stos = 
		let fromId = get_state_id sf in 
		let tmp_stos = ref [] in
			State_set.iter (fun a -> tmp_stos := (get_state_id (State a))::!tmp_stos) stos;
			try
				let tmp_struct = Hashtbl.find state_struct_tbl fromId in
				let tmp_toId = ref tmp_struct in
				List.iter (fun a -> if List.mem a !tmp_toId then () else tmp_toId := a :: !tmp_toId) !tmp_stos;
				Hashtbl.replace state_struct_tbl fromId !tmp_toId
			with Not_found -> 
				Hashtbl.add state_struct_tbl fromId !tmp_stos

	let rec prove cont modl = 
		match cont with
		| Basic b -> b
		| Cont (gamma, fml, id, levl, contl, contr) -> (Hashtbl.add sequents id (gamma, fml));(
			match fml with
			| Top -> prove (contl()) modl
			| Bottom -> prove (contr()) modl
			| Atomic (s, sl) -> let v = apply_atomic (Hashtbl.find modl.model_atomic_tbl s) sl in
									if v = Top then (prove (contl()) modl) else if v = Bottom then prove (contr()) modl else raise Error_proving_atomic
			| Neg Atomic (s, sl) -> let v = apply_atomic (Hashtbl.find modl.model_atomic_tbl s) sl in
										if v = Top then prove (contr()) modl else if v = Bottom then prove (contl()) modl else raise Error_proving_atomic
			| And (fml1, fml2) -> let id1 = new_id() and id2 = new_id() in
				prove (Cont (State_set.empty, fml1, (id1), (levl^"1"), (fun () -> Cont (State_set.empty, fml2, (id2), (levl^"2"), (fun () -> add_premises proof id (id1); add_premises proof id (id2); contl()), (fun () -> add_premises counterexample id (id2); contr()))), (fun () -> add_premises counterexample id (id1); contr()))) modl
			| Or (fml1, fml2) -> let id1 = new_id() and id2 = new_id() in
				prove (Cont (State_set.empty, fml1, (id1), (levl^"1"), (fun () -> add_premises proof id (id1); contl()), (fun () -> Cont (State_set.empty, fml2, (id2), (levl^"2"), (fun () -> add_premises proof id (id2); contl()), (fun () -> add_premises counterexample id (id1); add_premises counterexample id (id2); contr()))))) modl
			| AX (s, fml1, State sa) -> let sl = ((next sa modl.model_transitions)) in add_states (State sa) sl;
					prove ((make_ax_cont State_set.empty s fml1 id (levl^"1") sl contl contr)()) modl
			| EX (s, fml1, State sa) -> let sl = ((next sa modl.model_transitions)) in add_states (State sa) sl;
					prove ((make_ex_cont State_set.empty s fml1 id (levl^"1") sl contl contr)()) modl
			| AF (s, fml1, State sa) -> if State_set.mem sa gamma then 
									(add_merge merges levl gamma; prove (contr()) modl) else 
									(if state_in_merge merges levl sa then prove (contr()) modl else 
									let id1 = new_id() in
									let sl = ((next sa modl.model_transitions)) in add_states (State sa) sl;
									prove (Cont (State_set.empty, Formula.subst_s fml1 s (State sa), (id1), (levl^"1"), (fun () -> add_premises proof id (id1); contl()), (fun () -> add_premises counterexample id (id1); (make_af_cont (State_set.add sa gamma) s fml1 id levl sl contl contr)()))) modl)
			| EG (s, fml1, State sa) -> if State_set.mem sa gamma then 
									(add_merge merges levl gamma; prove (contl()) modl) else
									(if state_in_merge merges levl sa then prove (contl()) modl else 
									let id1 = new_id() in
									let sl = ((next sa modl.model_transitions)) in add_states (State sa) sl;
									prove (Cont (State_set.empty, Formula.subst_s fml1 s (State sa), (id1), (levl^"1"), (fun () -> add_premises proof id (id1);(make_eg_cont (State_set.add sa gamma) s fml1 id levl sl contl contr)()), (fun () -> add_premises counterexample id (id1); contr()))) modl)
			| AR(x, y, fml1, fml2, State sa) -> if (State_set.is_empty gamma) then Hashtbl.replace merges levl State_set.empty else add_merge merges levl gamma; if State_set.mem sa gamma then 
											(add_merge merges levl gamma; prove (contl()) modl) else 
											(if state_in_merge merges levl sa then prove (contl()) modl else
				let id1 = new_id() and id2 = new_id() in
				let sl = ((next sa modl.model_transitions)) in add_states (State sa) sl;
				prove (Cont (State_set.empty, Formula.subst_s fml2 y (State sa), (id2), (levl^"2"), (fun () -> Cont (State_set.empty, Formula.subst_s fml1 x (State sa), (id1), (levl^"1"), (fun () -> add_premises proof id (id1); add_premises proof id (id2); contl()), (fun () -> add_premises counterexample id (id1); (make_ar_cont (State_set.singleton sa) x y fml1 fml2 id levl sl contl contr)()))), (fun () -> add_premises counterexample id (id2); contr()))) modl)
			(*| AR(x, y, fml1, fml2, State sa) -> if (State_set.is_empty gamma) then Hashtbl.replace merges levl State_set.empty; if State_set.mem sa gamma then 
											(add_merge merges levl gamma; prove (contl()) modl) else 
											(if state_in_merge merges levl sa then prove (contl()) modl else
				let id1 = new_id() and id2 = new_id() in
				prove (Cont (State_set.empty, Formula.subst_s fml2 y (State sa), (id2), (levl^"2"), (fun () -> Cont (State_set.empty, Formula.subst_s fml1 x (State sa), (id1), (levl^"1"), (fun () -> add_premises proof id (id1); add_premises proof id (id2); contl()), (fun () -> add_premises counterexample id (id1); (make_ar_cont (State_set.add sa gamma) x y fml1 fml2 id levl (((next sa modl.model_transitions))) contl contr)()))), (fun () -> add_premises counterexample id (id+2); contr()))) modl) *)
			| EU (s, s', fml1, fml2, State sa) -> if (State_set.is_empty gamma) then Hashtbl.replace merges levl State_set.empty else add_merge merges levl gamma; if State_set.mem sa gamma then 
												(prove (contr()) modl) else
												( if state_in_merge merges levl sa then prove (contr()) modl else
									let id1 = new_id() and id2 = new_id() in
									let sl = ((next sa modl.model_transitions)) in add_states (State sa) sl;
									((prove (Cont (State_set.empty, Formula.subst_s fml2 s' (State sa), (id2), (levl^"2"), (fun () -> add_premises proof id (id2); contl()), (fun () -> Cont (State_set.empty, Formula.subst_s fml1 s (State sa), (id1), (levl^"1"), (fun () -> add_premises proof id (id1); (make_eu_cont (State_set.singleton sa) s s' fml1 fml2 id levl sl contl contr)()), (fun () -> add_premises counterexample id (id1); add_premises counterexample id (id2); contr()))))) modl)))  
			(*| EU (s, s', fml1, fml2, State sa) -> if (State_set.is_empty gamma) then Hashtbl.replace merges levl State_set.empty; if State_set.mem sa gamma then 
												(add_merge merges levl gamma; prove (contr()) modl) else
												( if state_in_merge merges levl sa then prove (contr()) modl else
									let id1 = new_id() and id2 = new_id() in
									((prove (Cont (State_set.empty, Formula.subst_s fml2 s' (State sa), (id2), (levl^"2"), (fun () -> add_premises proof id (id2); contl()), (fun () -> Cont (State_set.empty, Formula.subst_s fml1 s (State sa), (id1), (levl^"1"), (fun () -> add_premises proof id (id1); (make_eu_cont (State_set.add sa gamma) s s' fml1 fml2 id levl (((next sa modl.model_transitions))) contl contr)()), (fun () -> add_premises counterexample id (id1); add_premises counterexample id (id2); contr()))))) modl)))  *)
			| _ -> raise Unable_to_prove
			)
	
	let rec prove_model modl out outname ipaddr = 
		let spec_lst = modl.model_spec_list in
		let rec prove_lst lst = 
		match lst with
		| [] -> ()
		| (s, fml) :: lst' -> 
			((let nnf_fml = nnf fml in 
				print_endline (s^": "^(str_modl_fml modl.model_var_list (nnf_fml)));
				pre_process_merges (select_sub_fmls (sub_fmls nnf_fml "1"));
				let b = (prove (Cont (State_set.empty, Formula.subst_s (nnf_fml) (SVar "ini") modl.model_init_state, "0", "1", (fun () -> Basic true), (fun () -> Basic false))) modl) in
					print_endline (s ^ " is " ^ (if b then "true, proof output to \"output.out\"." else "false, counterexample output to \""^outname^"\".")); 
					output_result b s sequents (if b then proof else counterexample) out modl.model_var_list; 
					output_string out "***********************************ouput complete**************************************";
					flush out; 
	
					let (i,o) = Unix.open_connection (Unix.ADDR_INET (Unix.inet_addr_of_string ipaddr, 3333)) in 
					send_proof o;
					send_state_graph o;
					send_str o "end_adding";

					(*Thread.create receive_loop (i, o));*)
					receive_loop (i, o));
					(*Hashtbl.clear sequents; *)
					(*Hashtbl.clear proof);*)
					prove_lst lst') in prove_lst spec_lst
(*
	end
*)






