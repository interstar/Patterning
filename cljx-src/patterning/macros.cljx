(ns patterning.macros
 )

(defmacro optional-styled-primitive [args body] 
   (let [extra (conj args 'style)] 
     `(fn (~extra (~'->SShape ~'style ~body))
          (~args (~'->SShape {} ~body)) 
      )
   )
)
