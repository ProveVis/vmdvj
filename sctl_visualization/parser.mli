type token =
  | Module
  | Model
  | Var
  | Define
  | Init
  | Transition
  | Atomic
  | Spec
  | Int
  | Bool
  | Top
  | Bottom
  | AX
  | EX
  | AF
  | EG
  | AR
  | EU
  | Neg
  | Colon
  | Semicolon
  | Comma
  | Dot
  | LB1
  | RB1
  | LB2
  | RB2
  | LB3
  | RB3
  | And
  | Or
  | Equal
  | Assigno
  | Add
  | Minus
  | Mult
  | DotDot
  | Scalar
  | Nego
  | Ando
  | Oro
  | Non_equal
  | LT
  | GT
  | LE
  | GE
  | File_end
  | Id of (string)
  | I of (int)
  | B of (bool)

val input :
  (Lexing.lexbuf  -> token) -> Lexing.lexbuf -> (((string, Modul.modul) Hashtbl.t) * Modul.modul)
