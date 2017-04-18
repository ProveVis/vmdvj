open Term
open Formula

module Key = 
	struct
		type t = int array
		let compare = Pervasives.compare
	end;;

module State_set = Set.Make(Key)


type expr_type = 
	  Int_type of int * int
	| Bool_type
	| Scalar_type of string list
	| Module_type of modul 
and modul = 
{
	name : string;
	parameter_list : (string * expr_type) list;
	var_list : (string * expr_type) list;
	symbol_tbl : (string, expr) Hashtbl.t;
	init_assign : expr_module_instance list;
	transitions : (expr * ((int * expr) list)) list;
	atomic_tbl : (string, expr) Hashtbl.t;
	spec_list : (string * formula) list;
}
and modul_instance = 
{
	instance_name : string;
	init_state : expr_module_instance list;
	instance_transitions : (expr * ((int * expr) list)) list;
	instance_atomic_tbl : (string, expr) Hashtbl.t;
	instance_spec_list : (string * formula) list;
}
and model = 
{
	model_name : string;
	model_var_list : (string * expr_type) list;
	model_init_state : state;
	model_transitions : (expr * ((int * expr) list)) list;
	model_atomic_tbl : (string, expr) Hashtbl.t;
	model_spec_list : (string * formula) list;
}

let get_module_size modl =
	let rec get_var_list_size vl = 
	match vl with
	| [] -> 0
	| (s, Module_type m) :: vl' -> (get_var_list_size m.var_list) + (get_var_list_size vl')
	| vt :: vl' -> (get_var_list_size vl') + 1 in
	get_var_list_size modl.var_list

(************helper functions for instanciate the module***************)
let init_replace_parameters modl para_ins = 
	let rec replace_expr_module_instance emi = 
	match emi with
	| [] -> []
	| (Expr e) :: emi' -> (Expr (Hashtbl.fold (fun a b c -> replace_parameter_expr c a b) para_ins (expand_symbol_expr e modl.symbol_tbl))) :: (replace_expr_module_instance emi')
	| (Module_instance (s, es)) :: emi' -> (Module_instance (s, Hashtbl.fold (fun a b c -> replace_parameter_expr_list c a b) para_ins (expand_symbol_expr_list es modl.symbol_tbl))) :: (replace_expr_module_instance emi') in
	replace_expr_module_instance modl.init_assign
	
let trans_replace_parameters modl para_ins = 
	let rec rest_replace_parameters rests = 
	match rests with
	| [] -> []
	| (i, e) :: rests' -> (i, Hashtbl.fold (fun a b c -> replace_parameter_expr c a b) para_ins (expand_symbol_expr e modl.symbol_tbl)) :: (rest_replace_parameters rests')  in 
	let rec trans_replace trans = 
	match trans with
	| [] -> []
	| (e1, ies) :: trans' -> (Hashtbl.fold (fun a b c -> replace_parameter_expr c a b) para_ins (expand_symbol_expr e1 modl.symbol_tbl), rest_replace_parameters ies) :: (trans_replace trans') in
	trans_replace modl.transitions

let atomic_replace_parameters modl para_ins = 
	let n = Hashtbl.length modl.atomic_tbl in
	let tmp_atomic_tbl = Hashtbl.create n and
		ttmp_atomic_tbl = Hashtbl.create n in
	Hashtbl.iter (fun a b -> Hashtbl.add tmp_atomic_tbl a (expand_symbol_expr b modl.symbol_tbl)) modl.atomic_tbl;
	Hashtbl.iter (fun a b -> Hashtbl.add ttmp_atomic_tbl a (Hashtbl.fold (fun c d g -> replace_parameter_expr g c d) para_ins b)) tmp_atomic_tbl;
	ttmp_atomic_tbl
(*
	let tmp_atomic_tbl = Hashtbl.create (Hashtbl.length modl.atomic_tbl) in
	Hashtbl.iter (fun a b -> Hashtbl.add tmp_atomic_tbl a (Hashtbl.fold (fun c d g -> replace_parameter_expr (expand_symbol_expr g modl.symbol_tbl) c d) para_ins b)) modl.atomic_tbl; tmp_atomic_tbl
*)
let spec_replace_parameters modl para_ins = modl.spec_list


(*********************************************************************)

let get_modl_instance modl para_ins = 
{
	instance_name = modl.name;
	init_state = init_replace_parameters modl para_ins;
	instance_transitions = trans_replace_parameters modl para_ins;
	instance_atomic_tbl = atomic_replace_parameters modl para_ins;
	instance_spec_list = spec_replace_parameters modl para_ins;
}


(***********helper functions for building models**********************)
let rec build_var_list sb vl = 
		match vl with
		| [] -> []
		| (s, et) :: vl' -> let sb' = (if sb = "" then s else (sb^"."^s)) in
							match et with
							| Module_type m -> (build_var_list sb' m.var_list) @ (build_var_list sb vl')
							| t -> (sb', t) :: (build_var_list sb vl')

let rec raise_var_index_trans trans i = 
	let rec raise_var_index_rest rests j = 
	(match rests with
	| [] -> []
	| (ri, re) :: rests' -> (ri+j, raise_var_index_expr re j) :: (raise_var_index_rest rests' j)) in
	match trans with
	| [] -> []
	| (e, rests) :: trans' -> (raise_var_index_expr e i, raise_var_index_rest rests i) :: (raise_var_index_trans trans' i)

let get_para_tbl vl es = 
	let para_tbl = Hashtbl.create 5 in
	let rec get_para_tbl_from_list vll ess = 
		match vll with
		| (v, vt) :: vll' -> Hashtbl.add para_tbl v (List.hd ess); get_para_tbl_from_list vll' (List.tl ess)
		| [] -> () in
	get_para_tbl_from_list vl es; para_tbl

let product_trans trans1 trans2 = 
	let tmp_trans = ref [] in
	let rec connect_trans e1 ies1 t = 
	(match t with
	| [] -> ()
	| (e2, ies2) :: t' -> tmp_trans := (Ando (e1, e2), ies1 @ ies2) :: !tmp_trans) in
	let rec tmp_product_trans t1 t2 = 
		match t1 with
		| [] -> ()
		| (e, ies) :: t1' -> connect_trans e ies t2; tmp_product_trans t1' t2 in
	tmp_product_trans trans1 trans2; !tmp_trans

let rec merge_inis inis i ia = 
	match inis with
	| [] -> ()
	| (Expr (Const c)) :: inis' -> ia.(i) <- c; merge_inis inis' (i+1) ia
	| _ -> print_endline "error merging initial states."; exit 1


(*********************************************************************)
let build_model modl moduls para_ins = 
(*	let modul_instance_tbl = Hashtbl.create 5 in *)
	let vl = build_var_list "" modl.var_list in
	let ia = Array.make (List.length vl) 0 in
	let modul_inst = get_modl_instance modl para_ins in
	let trans = ref (modul_inst.instance_transitions) in
	let rec build_init_state_trans emi i = 
		(match emi with
		| [] -> ()
		| (Expr e) :: emi' -> (let ee = eval_expr e in 
								(match ee with
								| Const c -> (ia.(i) <- c)
								| _ -> print_endline ((str_expr ee)^" is not a constant."); exit 1)); build_init_state_trans emi' (i+1)
		| (Module_instance (s, es)) :: emi' -> (let md = Hashtbl.find moduls s in
												let mdi = get_modl_instance md (get_para_tbl md.parameter_list es) in
												let mi = get_module_size (Hashtbl.find moduls s) in 
												merge_inis mdi.init_state i ia;
												trans := product_trans !trans (raise_var_index_trans mdi.instance_transitions i);
												build_init_state_trans emi' (i+mi))) in
	build_init_state_trans modul_inst.init_state 0;
{
	model_name = modl.name;
	model_var_list = vl;
	model_init_state = State ia;
	model_transitions = !trans;
	model_atomic_tbl = modul_inst.instance_atomic_tbl;
	model_spec_list = modul_inst.instance_spec_list;
}

let str_expr_type et = 
	match et with
	| Int_type (i1, i2) -> "int("^(string_of_int i1)^", "^(string_of_int i2)^")"
	| Bool_type -> "bool"
	| Scalar_type sl -> let rec str_str_list strs = if List.length strs <> 0 then (List.hd strs)^(str_str_list (List.tl strs)^" ") else "" in "{"^(str_str_list sl)^"}"
	| Module_type m -> m.name

let rec str_state_rest strest = 
	match strest with
	| [] -> ""
	| (i, e) :: strest' -> (string_of_int i)^":="^(str_expr e)^"; "^(str_state_rest strest')


let print_module modl = 
	print_endline "******Print Module*******";
	print_endline "*name: ";
	print_endline modl.name;
	print_endline "*parameters: ";
	List.iter (fun a -> print_endline ((fst a)^": "^(str_expr_type (snd a)))) modl.parameter_list;
	print_endline "*variables: ";
	List.iter (fun a -> print_endline ((fst a)^": "^(str_expr_type (snd a)))) modl.var_list;
	print_endline "*symbols:";
	Hashtbl.iter (fun a b -> print_endline (a^":="^(str_expr b))) modl.symbol_tbl;
	print_endline "*init assign:";
	List.iter (fun a -> print_endline (
										match a with
										| Expr e -> str_expr e
										| Module_instance (s, es) -> (s^"("^(str_expr_list es)^")"))) modl.init_assign; 
	print_endline "*transitions:";
	List.iter (fun a -> print_endline ((str_expr (fst a))^" {"^(str_state_rest (snd a))^"}")) modl.transitions;
	print_endline "*atomic formulae:";
	Hashtbl.iter (fun a b -> print_endline (a^":="^(str_expr b))) modl.atomic_tbl;
	print_endline "*specs:";
	List.iter (fun a -> print_endline ((fst a)^":="^(fml_to_string (snd a)))) modl.spec_list;
	print_endline "****End Print Module*****"

let print_model modl = 
	print_endline "******Print Model*******";
	print_endline "*name: ";
	print_endline modl.model_name;
	print_endline "*init state:";
	print_endline (str_state modl.model_init_state); 
	print_endline "*transitions:";
	List.iter (fun a -> print_endline ((str_expr (fst a))^" {"^(str_state_rest (snd a))^"}")) modl.model_transitions;
	print_endline "*atomic formulae:";
	Hashtbl.iter (fun a b -> print_endline (a^":="^(str_expr b))) modl.model_atomic_tbl;
	print_endline "*specs:";
	List.iter (fun a -> print_endline ((fst a)^":="^(fml_to_string (snd a)))) modl.model_spec_list;
	print_endline "****End Print Model*****"

let rec get_new_constant_array ia rests cia = 
	match rests with
	| [] -> cia
	| (i, e) :: rests' -> (match eval_with_state e (State ia) with
							| Const c -> get_new_constant_array ia rests' (cia.(i) <- c; cia)
							| _ -> print_endline "error constructing new states."; exit 1)

(*computing next state, new version*)
let rec next ia trans = 
	let ss = ref (State_set.empty) in
	let rec eval_trans trans = 
	match trans with
	| (e, rests) :: trans' -> (match (eval_with_array e ia) with
								| Const 1 -> ss := State_set.add (get_new_constant_array ia rests (Array.copy ia)) !ss; eval_trans trans'
								| Const (-1) -> eval_trans trans'
								| _ -> print_endline ("error evaluating expression "^(str_expr e)^"."); exit 1)
	| [] -> () in 
	eval_trans trans; !ss



let apply_atomic e sl = 
	let b = eval_expr_with_states e sl in
	match b with
	| Const 1 -> Top
	| Const (-1) -> Bottom
	| _ -> print_endline ((str_expr b)^" is not a constant, in apply atomic."); exit 1 

let str_modl_state vt sa = 
	let rec str_type_value vt ia i = 
		match vt with
		| [] -> ""
		| [(v, t)] -> (match t with
						| Int_type (i1, i2) -> (v^":="^(string_of_int (ia.(i))))
						| Bool_type -> (v^":="^(if (ia.(i)=1) then "true" else (if (ia.(i)=(-1)) then "false" else "unknown_bool_value")))
						| Scalar_type sl -> (v^":="^(List.nth sl (ia.(i))))
						| Module_type m -> print_endline "Error: state not expanded."; exit (-1))
		| (v, t) :: vt' -> (match t with
						| Int_type (i1, i2) -> (v^":="^(string_of_int (ia.(i)))^";")
						| Bool_type -> (v^":="^(if (ia.(i)=1) then "true" else (if (ia.(i)=(-1)) then "false" else "unknown_bool_value"))^";")
						| Scalar_type sl -> (v^":="^(List.nth sl (ia.(i)))^";")
						| Module_type m -> print_endline "Error: state not expanded."; exit (-1)) ^ (str_type_value vt' ia (i+1)) in
	"{"^(str_type_value vt sa 0)^"}"

let str_modl_state_or_var vt st = 
	match st with
	| SVar v -> v
	| State sa -> str_modl_state vt sa

let rec str_modl_state_or_var_list vt sts = 
	match sts with
	| [] -> ""
	| [st] -> str_modl_state_or_var vt st
	| st :: sts' -> (str_modl_state_or_var vt st) ^","^(str_modl_state_or_var_list vt sts')

let rec str_modl_fml vt fml = 
	match fml with
	| Top -> "TRUE"
	| Bottom -> "FALSE"
	| Atomic (e, sl) -> (e) ^ "("^ (str_modl_state_or_var_list vt sl) ^")"
	| Neg fml1 -> "(not " ^ (fml_to_string fml1) ^ ")"
	| And (fml1, fml2) -> (fml_to_string fml1) ^ "/\\" ^ (fml_to_string fml2)
	| Or (fml1, fml2) -> (fml_to_string fml1) ^ "\\/" ^ (fml_to_string fml2)
	| AX (s, fml1, s') -> "AX(" ^ (str_state s) ^ ", (" ^ (fml_to_string fml1) ^ "), " ^ (str_modl_state_or_var vt s') ^ ")"
	| EX (s, fml1, s') -> "EX(" ^ (str_state s) ^ ", (" ^ (fml_to_string fml1) ^ "), " ^ (str_modl_state_or_var vt s') ^ ")"
	| AF (s, fml1, s') -> "AF(" ^ (str_state s) ^ ", (" ^ (fml_to_string fml1) ^ "), " ^ (str_modl_state_or_var vt s') ^ ")"
	| EG (s, fml1, s') -> "EG(" ^ (str_state s) ^ ", (" ^ (fml_to_string fml1) ^ "), " ^ (str_modl_state_or_var vt s') ^ ")"
	| AR (s, s', fml1, fml2, s'') -> "AR(" ^ (str_state s) ^ ", " ^ (str_modl_state_or_var vt s') ^ ", (" ^ (fml_to_string fml1) ^ "), (" ^ (fml_to_string fml2) ^ "), " ^ (str_modl_state_or_var vt s'') ^ ")"
	| EU (s, s', fml1, fml2, s'') -> "EU(" ^ (str_state s) ^ ", " ^ (str_modl_state_or_var vt s') ^ ", (" ^ (fml_to_string fml1) ^ "), (" ^ (fml_to_string fml2) ^ "), " ^ (str_modl_state_or_var vt s'') ^ ")"



	
