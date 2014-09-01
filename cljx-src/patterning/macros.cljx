(ns patterning.macros
 )

(defmacro optional-styled-primitive [args body] 
   (let [extra (conj args 'style)] 
     `(fn (~extra (~'group (~'->SShape ~'style ~body)))
         (~args  (~'group (~'->SShape {} ~body))) 
      )
   )
)
