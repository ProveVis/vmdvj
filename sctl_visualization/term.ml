
type expr = 
	  Parameter of string	
	| Symbol of string
	| Var of int
	| State_expr of int * expr
	| Const of int
	| Negi of expr	
	| Add of expr * expr
	| Minus of expr * expr
	| Mult of expr * expr
	| Ando of expr * expr
	| Oro of expr * expr
	| Negb of expr
	| Equal of expr * expr
	| LT of expr * expr
	| GT of expr * expr
	| LE of expr * expr
	| GE of expr * expr
and expr_module_instance = 
	  Expr of expr
	| Module_instance of string * (expr list)

and state = 
	  SVar of string
	| State of int array

let get_array_from_state s = 
	match s with
	| SVar v -> Array.make 1 0
	| State ia -> ia

(*replace parameters by instances*)
let rec replace_parameter_expr exp pv e = 
	match exp with
	| Parameter s -> if pv = s then e else exp
	| State_expr (i, e1) -> State_expr (i, replace_parameter_expr e1 pv e)
	| Negi e1 -> Negi (replace_parameter_expr e1 pv e)
	| Negb e1 -> Negb (replace_parameter_expr e1 pv e)
	| Add (e1, e2) -> Add (replace_parameter_expr e1 pv e, replace_parameter_expr e2 pv e)
	| Minus (e1, e2) -> Minus (replace_parameter_expr e1 pv e, replace_parameter_expr e2 pv e)
	| Mult (e1, e2) -> Mult (replace_parameter_expr e1 pv e, replace_parameter_expr e2 pv e)
	| Ando (e1, e2) -> Ando (replace_parameter_expr e1 pv e, replace_parameter_expr e2 pv e)
	| Oro (e1, e2) -> Oro (replace_parameter_expr e1 pv e, replace_parameter_expr e2 pv e)
	| Equal (e1, e2) -> Equal (replace_parameter_expr e1 pv e, replace_parameter_expr e2 pv e)
	| LT (e1, e2) -> LT (replace_parameter_expr e1 pv e, replace_parameter_expr e2 pv e)
	| GT (e1, e2) -> GT (replace_parameter_expr e1 pv e, replace_parameter_expr e2 pv e)
	| LE (e1, e2) -> LE (replace_parameter_expr e1 pv e, replace_parameter_expr e2 pv e)
	| GE (e1, e2) -> GE (replace_parameter_expr e1 pv e, replace_parameter_expr e2 pv e)
	| _ -> exp

let rec replace_parameter_expr_list expl pv e = 
	match expl with
	| [] -> []
	| exp :: expl' -> (replace_parameter_expr exp pv e) :: (replace_parameter_expr_list expl' pv e)

(*expand symbol definition*)
let rec expand_symbol_expr e stbl = 
	match e with
	| Symbol s -> let se = Hashtbl.find stbl s in expand_symbol_expr se stbl
	| State_expr (i, e1) -> State_expr (i, expand_symbol_expr e1 stbl)
	| Negi e1 -> Negi (expand_symbol_expr e1 stbl)
	| Negb e1 -> Negb (expand_symbol_expr e1 stbl)
	| Add (e1, e2) -> Add (expand_symbol_expr e1 stbl, expand_symbol_expr e2 stbl)
	| Minus (e1, e2) -> Minus (expand_symbol_expr e1 stbl, expand_symbol_expr e2 stbl)
	| Mult (e1, e2) -> Mult (expand_symbol_expr e1 stbl, expand_symbol_expr e2 stbl)
	| Ando (e1, e2) -> Ando (expand_symbol_expr e1 stbl, expand_symbol_expr e2 stbl)
	| Oro (e1, e2) -> Oro (expand_symbol_expr e1 stbl, expand_symbol_expr e2 stbl)
	| Equal (e1, e2) -> Equal (expand_symbol_expr e1 stbl, expand_symbol_expr e2 stbl)
	| LT (e1, e2) -> LT (expand_symbol_expr e1 stbl, expand_symbol_expr e2 stbl)
	| GT (e1, e2) -> GT (expand_symbol_expr e1 stbl, expand_symbol_expr e2 stbl)
	| LE (e1, e2) -> LE (expand_symbol_expr e1 stbl, expand_symbol_expr e2 stbl)
	| GE (e1, e2) -> GE (expand_symbol_expr e1 stbl, expand_symbol_expr e2 stbl)
	| _ -> e

let rec expand_symbol_expr_list el stbl = 
	match el with
	| [] -> []
	| e :: el' -> (expand_symbol_expr e stbl) :: (expand_symbol_expr_list el' stbl)
  
(*raise index of variables*)
let rec raise_var_index_expr e i = 
	match e with
	| Var vi -> Var (vi+i)
	| State_expr (si, e1) -> State_expr (si, raise_var_index_expr e1 i)
	| Negi e1 -> Negi (raise_var_index_expr e1 i)
	| Negb e1 -> Negb (raise_var_index_expr e1 i)
	| Add (e1, e2) -> Add (raise_var_index_expr e1 i, raise_var_index_expr e2 i)	
	| Minus (e1, e2) -> Minus (raise_var_index_expr e1 i, raise_var_index_expr e2 i)
	| Mult (e1, e2) -> Mult (raise_var_index_expr e1 i, raise_var_index_expr e2 i)
	| Ando (e1, e2) -> Ando (raise_var_index_expr e1 i, raise_var_index_expr e2 i)
	| Oro (e1, e2) -> Oro (raise_var_index_expr e1 i, raise_var_index_expr e2 i)
	| Equal (e1, e2) -> Equal (raise_var_index_expr e1 i, raise_var_index_expr e2 i)
	| LT (e1, e2) -> LT (raise_var_index_expr e1 i, raise_var_index_expr e2 i)
	| GT (e1, e2) -> GT (raise_var_index_expr e1 i, raise_var_index_expr e2 i)
	| LE (e1, e2) -> LE (raise_var_index_expr e1 i, raise_var_index_expr e2 i)
	| GE (e1, e2) -> GE (raise_var_index_expr e1 i, raise_var_index_expr e2 i)
	| _ -> e

let rec raise_var_index_expr_list es i = 
	match es with
	| [] -> []
	| e :: es' -> (raise_var_index_expr e i) :: (raise_var_index_expr_list es' i)




(* expr to string *)
let rec str_expr e = 
	match e with
	| Parameter s -> s
	| Symbol s -> "(Symbol "^s^")"
	| Var i -> "(var "^(string_of_int i)^")"
	| State_expr (i, e1) -> (string_of_int i)^"("^(str_expr e1)^")"
	| Const i -> string_of_int i
	| Negi e1 -> "(- "^(str_expr e1)^")"
	| Add (e1, e2) -> "("^(str_expr e1)^"+"^(str_expr e2)^")"
	| Minus (e1, e2) -> "("^(str_expr e1)^"-"^(str_expr e2)^")"
	| Mult (e1, e2) -> "("^(str_expr e1)^"*"^(str_expr e2)^")"
	| Ando (e1, e2) -> "("^(str_expr e1)^"&&"^(str_expr e2)^")"
	| Oro (e1, e2) -> "("^(str_expr e1)^"||"^(str_expr e2)^")"
	| Negb e1 -> "(Negb "^(str_expr e1)^")"
	| Equal (e1, e2) -> "("^(str_expr e1)^"="^(str_expr e2)^")"
	| LT (e1, e2) -> "("^(str_expr e1)^"<"^(str_expr e2)^")"
	| GT (e1, e2) -> "("^(str_expr e1)^">"^(str_expr e2)^")"
	| LE (e1, e2) -> "("^(str_expr e1)^"<="^(str_expr e2)^")"
	| GE (e1, e2) -> "("^(str_expr e1)^">="^(str_expr e2)^")"

let rec str_expr_list es = 
	match es with	
	| [] -> ""
	| [e] -> str_expr e
	| e :: es' -> (str_expr e)^","^(str_expr_list es')

(* state to string *)
let str_state st = 
	let rec str_int_array ia = 
		let al = Array.length ia in
			if al = 0 then "" else 
				if al = 1 then string_of_int (ia.(0)) else (string_of_int (ia.(0)))^","^(str_int_array (Array.sub ia 1 (al-1))) in 
	match st with
	| SVar sv -> sv
	| State ia -> "["^str_int_array ia^"]"

let rec str_state_list sts = 
	match sts with
	| [] -> ""
	| [st] -> str_state st
	| st :: sts' -> (str_state st)^","^(str_state_list sts')

(* expression equal*)
let compare_expr e1 e2 = Pervasives.compare e1 e2
let compare_state s1 s2 = 
	match (s1, s2) with
	| (SVar sv1, SVar sv2) -> String.compare sv1 sv2
	| (State ia1, State ia2) -> Pervasives.compare ia1 ia2
	| _ -> -1


(* regular evaluating expressions *)
let rec eval_expr e = 
	match e with
	| Negi e1 -> let e2 = eval_expr e1 in 
				(match e2 with
				| Const i -> Const (-i)
				| Negi e3 -> e3
				| _ -> Negi e2
				)
	| Add (e1, e2) -> let e3 = eval_expr e1 and e4 = eval_expr e2 in
						(match (e3, e4) with
						| (Const i1, Const i2) -> Const (i1 + i2)
						| _ -> Add (e3, e4)
						)
	| Minus (e1, e2) -> let e3 = eval_expr e1 and e4 = eval_expr e2 in
						(match (e3, e4) with
						| (Const i1, Const i2) -> Const (i1 - i2)
						| _ -> Minus (e3, e4)
						)
	| Mult (e1, e2) -> let e3 = eval_expr e1 and e4 = eval_expr e2 in
						(match (e3, e4) with
						| (Const i1, Const i2) -> Const (i1 * i2)
						| _ -> Mult (e3, e4)
						)
	| Negb e1 -> let e2 = eval_expr e1 in
				(match e2 with
				| Const i -> Const (-i)
				| Negb e3 -> e3
				| _ -> Negb e2
				)
	| Ando (e1, e2) -> let e3 = eval_expr e1 and e4 = eval_expr e2 in
						(match (e3, e4) with
						| (Const i1, Const i2) -> Const (if i1 > i2 then i2 else i1)
						| _ -> Ando (e3, e4)
						)
	| Oro (e1, e2) -> let e3 = eval_expr e1 and e4 = eval_expr e2 in
						(match (e3, e4) with
						| (Const i1, Const i2) -> Const (if i1 < i2 then i2 else i1)
						| _ -> Oro (e3, e4)
						)
	(*need to be carefully checked*)
	| Equal (e1, e2) -> let e3 = eval_expr e1 and e4 = eval_expr e2 in
						if compare_expr e3 e4 = 0 then Const 1 else Const (-1) 
	| LT (e1, e2) -> let e3 = eval_expr e1 and e4 = eval_expr e2 in
						(match (e3, e4) with
						| (Const i1, Const i2) -> Const (if i1 < i2 then 1 else (-1))
						| _ -> LT (e3, e4)
						)
	| GT (e1, e2) -> let e3 = eval_expr e1 and e4 = eval_expr e2 in
						(match (e3, e4) with
						| (Const i1, Const i2) -> Const (if i1 > i2 then 1 else (-1))
						| _ -> GT (e3, e4)
						)
	| LE (e1, e2) -> let e3 = eval_expr e1 and e4 = eval_expr e2 in
						(match (e3, e4) with
						| (Const i1, Const i2) -> Const (if i1 <= i2 then 1 else (-1))
						| _ -> LE (e3, e4)
						)
	| GE (e1, e2) -> let e3 = eval_expr e1 and e4 = eval_expr e2 in
						(match (e3, e4) with
						| (Const i1, Const i2) -> Const (if i1 >= i2 then 1 else (-1))
						| _ -> GE (e3, e4)
						)
	| _ -> e

(* useful in evaluating expressions in atomic fOromulae *)
let rec eval_expr_with_states e sts = 
	match e with
	| State_expr (v1, e1) -> eval_with_state e1 (List.nth sts v1)
	| Negi e1 -> let e2 = eval_expr_with_states e1 sts in
				(match e2 with
				| Const i -> Const (-i)
				| Negi e3 -> e3
				| _ -> Negi e2
				)
	| Add (e1, e2) -> let e3 = eval_expr_with_states e1 sts and e4 = eval_expr_with_states e2 sts in
						(match (e3, e4) with
						| (Const i1, Const i2) -> Const (i1 + i2)
						| _ -> Add (e3, e4)
						)
	| Minus (e1, e2) -> let e3 = eval_expr_with_states e1 sts and e4 = eval_expr_with_states e2 sts in
						(match (e3, e4) with
						| (Const i1, Const i2) -> Const (i1 - i2)
						| _ -> Minus (e3, e4)
						)
	| Mult (e1, e2) -> let e3 = eval_expr_with_states e1 sts and e4 = eval_expr_with_states e2 sts in
						(match (e3, e4) with
						| (Const i1, Const i2) -> Const (i1 * i2)
						| _ -> Mult (e3, e4)
						)
	| Negb e1 -> let e2 = eval_expr_with_states e1 sts in
				(match e2 with
				| Const i -> Const (-i)
				| Negb e3 -> e3
				| _ -> Negb e2
				)
	| Ando (e1, e2) -> let e3 = eval_expr_with_states e1 sts and e4 = eval_expr_with_states e2 sts in
						(match (e3, e4) with
						| (Const i1, Const i2) -> Const (if i1 > i2 then i2 else i1)
						| _ -> Ando (e3, e4)
						)
	| Oro (e1, e2) -> let e3 = eval_expr_with_states e1 sts and e4 = eval_expr_with_states e2 sts in
						(match (e3, e4) with
						| (Const i1, Const i2) -> Const (if i1 < i2 then i2 else i1)
						| _ -> Oro (e3, e4)
						)
	(*need to be carefully checked*)
	| Equal (e1, e2) -> let e3 = eval_expr_with_states e1 sts and e4 = eval_expr_with_states e2 sts in
						if compare_expr e3 e4 = 0 then Const 1 else Const (-1) 
	| LT (e1, e2) -> let e3 = eval_expr_with_states e1 sts and e4 = eval_expr_with_states e2 sts in
						(match (e3, e4) with
						| (Const i1, Const i2) -> Const (if i1 < i2 then 1 else (-1))
						| _ -> LT (e3, e4)
						)
	| GT (e1, e2) -> let e3 = eval_expr_with_states e1 sts and e4 = eval_expr_with_states e2 sts in
						(match (e3, e4) with
						| (Const i1, Const i2) -> Const (if i1 > i2 then 1 else (-1))
						| _ -> GT (e3, e4)
						)
	| LE (e1, e2) -> let e3 = eval_expr_with_states e1 sts and e4 = eval_expr_with_states e2 sts in
						(match (e3, e4) with
						| (Const i1, Const i2) -> Const (if i1 <= i2 then 1 else (-1))
						| _ -> LE (e3, e4)
						)
	| GE (e1, e2) -> let e3 = eval_expr_with_states e1 sts and e4 = eval_expr_with_states e2 sts in
						(match (e3, e4) with
						| (Const i1, Const i2) -> Const (if i1 >= i2 then 1 else (-1))
						| _ -> GE (e3, e4)
						)
	| _ -> e
and eval_with_state e st = 
	match st with
	| State ia -> (match e with
					| Var i -> Const (ia.(i))
					| Negi e1 -> let e2 = eval_with_state e1 st in 
								(match e2 with
								| Const i -> Const (-i)
								| Negi e3 -> e3
								| _ -> Negi e2)
					| Add (e1, e2) -> let e3 = eval_with_state e1 st and e4 = eval_with_state e2 st in
										(match (e3, e4) with
										| (Const i1, Const i2) -> Const (i1+i2)
										| _ -> Add (e3, e4))
					| Minus (e1, e2) -> let e3 = eval_with_state e1 st and e4 = eval_with_state e2 st in
										(match (e3, e4) with
										| (Const i1, Const i2) -> Const (i1-i2)
										| _ -> Minus (e3, e4))
					| Mult (e1, e2) -> let e3 = eval_with_state e1 st and e4 = eval_with_state e2 st in
										(match (e3, e4) with
										| (Const i1, Const i2) -> Const (i1*i2)
										| _ -> Mult (e3, e4))
					| Negb e1 -> let e2 = eval_with_state e1 st in 
								(match e2 with
								| Const i -> Const (-i)
								| Negb e3 -> e3
								| _ -> Negb e2)
					| Ando (e1, e2) -> let e3 = eval_with_state e1 st and e4 = eval_with_state e2 st in
										(match (e3, e4) with
										| (Const i1, Const i2) -> Const (if i1 > i2 then i2 else i1)
										| _ -> Ando (e3, e4)
										)
					| Oro (e1, e2) -> let e3 = eval_with_state e1 st and e4 = eval_with_state e2 st in
										(match (e3, e4) with
										| (Const i1, Const i2) -> Const (if i1 < i2 then i2 else i1)
										| _ -> Oro (e3, e4)
										)
					(*need to be carefully checked*)
					| Equal (e1, e2) -> let e3 = eval_with_state e1 st and e4 = eval_with_state e2 st in
											if compare_expr e3 e4 = 0 then Const 1 else Const (-1) 
					| LT (e1, e2) -> let e3 = eval_with_state e1 st and e4 = eval_with_state e2 st in
										(match (e3, e4) with
										| (Const i1, Const i2) -> Const (if i1 < i2 then 1 else (-1))
										| _ -> LT (e3, e4)
										)
					| GT (e1, e2) -> let e3 = eval_with_state e1 st and e4 = eval_with_state e2 st in
										(match (e3, e4) with
										| (Const i1, Const i2) -> Const (if i1 > i2 then 1 else (-1))
										| _ -> GT (e3, e4)
										)
					| LE (e1, e2) -> let e3 = eval_with_state e1 st and e4 = eval_with_state e2 st in
										(match (e3, e4) with
										| (Const i1, Const i2) -> Const (if i1 <= i2 then 1 else (-1))
										| _ -> LE (e3, e4)
										)
					| GE (e1, e2) -> let e3 = eval_with_state e1 st and e4 = eval_with_state e2 st in
										(match (e3, e4) with
										| (Const i1, Const i2) -> Const (if i1 >= i2 then 1 else (-1))
										| _ -> GE (e3, e4)
										)

					| _ -> e
					)
	| SVar sv -> e

let rec eval_with_array e ia = 
	match e with
	| Var i -> Const (ia.(i))
	| Negi e1 -> let e2 = eval_with_array e1 ia in 
				(match e2 with
				| Const i -> Const (-i)
				| Negi e3 -> e3
				| _ -> Negi e2)
	| Add (e1, e2) -> let e3 = eval_with_array e1 ia and e4 = eval_with_array e2 ia in
						(match (e3, e4) with
						| (Const i1, Const i2) -> Const (i1+i2)
						| _ -> Add (e3, e4))
	| Minus (e1, e2) -> let e3 = eval_with_array e1 ia and e4 = eval_with_array e2 ia in
						(match (e3, e4) with
						| (Const i1, Const i2) -> Const (i1-i2)
						| _ -> Minus (e3, e4))
	| Mult (e1, e2) -> let e3 = eval_with_array e1 ia and e4 = eval_with_array e2 ia in
						(match (e3, e4) with
						| (Const i1, Const i2) -> Const (i1*i2)
						| _ -> Mult (e3, e4))
	| Negb e1 -> let e2 = eval_with_array e1 ia in 
				(match e2 with
				| Const i -> Const (-i)
				| Negb e3 -> e3
				| _ -> Negb e2)
	| Ando (e1, e2) -> let e3 = eval_with_array e1 ia and e4 = eval_with_array e2 ia in
						(match (e3, e4) with
						| (Const i1, Const i2) -> Const (if i1 > i2 then i2 else i1)
						| _ -> Ando (e3, e4)
						)
	| Oro (e1, e2) -> let e3 = eval_with_array e1 ia and e4 = eval_with_array e2 ia in
						(match (e3, e4) with
						| (Const i1, Const i2) -> Const (if i1 < i2 then i2 else i1)
						| _ -> Oro (e3, e4)
						)
	(*need to be carefully checked*)
	| Equal (e1, e2) -> let e3 = eval_with_array e1 ia and e4 = eval_with_array e2 ia in
							if compare_expr e3 e4 = 0 then Const 1 else Const (-1) 
	| LT (e1, e2) -> let e3 = eval_with_array e1 ia and e4 = eval_with_array e2 ia in
						(match (e3, e4) with
						| (Const i1, Const i2) -> Const (if i1 < i2 then 1 else (-1))
						| _ -> LT (e3, e4)
						)
	| GT (e1, e2) -> let e3 = eval_with_array e1 ia and e4 = eval_with_array e2 ia in
						(match (e3, e4) with
						| (Const i1, Const i2) -> Const (if i1 > i2 then 1 else (-1))
						| _ -> GT (e3, e4)
						)
	| LE (e1, e2) -> let e3 = eval_with_array e1 ia and e4 = eval_with_array e2 ia in
						(match (e3, e4) with
						| (Const i1, Const i2) -> Const (if i1 <= i2 then 1 else (-1))
						| _ -> LE (e3, e4)
						)
	| GE (e1, e2) -> let e3 = eval_with_array e1 ia and e4 = eval_with_array e2 ia in
						(match (e3, e4) with
						| (Const i1, Const i2) -> Const (if i1 >= i2 then 1 else (-1))
						| _ -> GE (e3, e4)
						)
	| _ -> e




