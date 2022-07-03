##W trakcie przygotowywania

###Typy
- [x] ANIMO
- [x] APPLICATION
- [x] ARRAY
- [x] BEHAVIOUR
- [x] BOOL
- [x] BUTTON
- [ ] CANVAS
- [x] CANVAS_OBSERVER
- [x] CLASS
- [x] CNVLOADER
- [x] COMPLEXCONDITION
- [x] CONDITION
- [x] DATABASE
- [ ] DIALOG
- [x] DOUBLE
- [ ] EDITBOX
- [ ] EDITGROUP
- [x] EPISODE
- [x] EXPRESSION
- [ ] FIFO
- [x] FILTER
- [x] FONT
- [ ] GRBUFFER
- [x] GROUP
- [x] IMAGE
- [x] INTEGER
- [ ] INTERNET
- [ ] JOYSTICK
- [x] KEYBOARD
- [ ] LIFO
- [x] MOUSE
- [ ] MOVIE
- [x] MULTIARRAY
- [x] MUSIC
- [ ] NETCLIENT
- [ ] NETPEER
- [ ] NETSERVER
- [ ] PAINTER
- [ ] PATH
- [x] PATTERN
- [ ] RAND
- [x] SCENE
- [ ] SCROLL
- [x] SEQUENCE
- [ ] SIGNAL
- [x] SOUND
- [x] STATICFILTER
- [x] STRING
- [x] STRUCT
- [ ] SYSTEM
- [x] TEXT
- [ ] VIRTUALGRAPHICSOBJECT
- [x] TIMER
- [x] VECTOR
- [x] WORLD

###Instrucje
- [x] @BOOL
- [x] @BREAK
- [ ] @CONTINUE
- [ ] @CONV
- [ ] @CREATE
- [x] @DOUBLE
- [ ] @FOR
- [ ] @GETAPPLICATIONNAME
- [ ] @GETCURRENTSCENE
- [x] @IF
- [x] @INT
- [x] @LOOP
- [ ] @MSGBOX
- [ ] @ONEBREAK
- [x] @RETURN
- [ ] @RUNONTIMER
- [x] @STRING
- [ ] @VALUE
- [ ] @WHILE

##Disclaimer: pola, sygnały bądź typy z gwiazdką na końcu to te, których automatyczny skaner nie znalazł ani razu w skryptach gier z Reksiem
W oparciu o arkusz pod linkiem https://docs.google.com/spreadsheets/d/132gkNJ0_GsZXAX17Jlp9qo1DMSRpbPdLWJpIBTCpvhI/edit?usp=drivesdk

###Animo
- [x] DESCRIPTION
- [x] FILENAME
- [x] FPS
- [x] MONITORCOLLISION
- [x] MONITORCOLLISIONALPHA
- [x] ONCLICK
- [x] ONCOLLISION
- [ ] ONCOLLISIONFINISHED*
- [ ] ONDONE*
- [x] ONFINISHED
- [ ] ONFIRSTFRAME*
- [x] ONFOCUSOFF
- [x] ONFOCUSON
- [x] ONFRAMECHANGED
- [x] ONINIT
- [ ] ONPAUSED*
- [x] ONRELEASE
- [ ] ONRESUMED*
- [x] ONSIGNAL
- [x] ONSTARTED
- [x] PRELOAD
- [x] PRIORITY
- [x] RELEASE
- [x] TOCANVAS
- [x] VISIBLE

###Application
- [x] AUTHOR
- [x] BLOOMOO_VERSION
- [x] CREATIONTIME
- [x] DESCRIPTION
- [x] EPISODES
- [x] LASTMODIFYTIME
- [x] PATH
- [x] STARTWITH
- [x] VERSION

###Array
- [x] DESCRIPTION
- [x] ONINIT

###Behaviour
- [x] CODE
- [x] CONDITION
- [x] DESCRIPTION

###Bool
- [x] DESCRIPTION
- [x] ONBRUTALCHANGED
- [x] ONCHANGED
- [ ] ONDONE*
- [x] ONINIT
- [ ] ONNETCHANGED*
- [ ] ONSIGNAL*
- [x] TOINI
- [x] VALUE

###Button
- [x] DESCRIPTION
- [x] DRAGGABLE
- [x] ENABLE
- [x] GFXONCLICK
- [x] GFXONMOVE
- [x] GFXSTANDARD
- [x] ONACTION
- [x] ONCLICKED
- [ ] ONDONE*
- [ ] ONDRAGGING*
- [x] ONENDDRAGGING
- [x] ONFOCUSOFF
- [x] ONFOCUSON
- [x] ONINIT
- [ ] ONPAUSED*
- [x] ONRELEASED
- [ ] ONSIGNAL*
- [x] ONSTARTDRAGGING
- [x] PRIORITY
- [x] RECT
- [ ] SNDONCLICK*
- [x] SNDONMOVE
- [ ] SNDSTANDARD*
- [x] VISIBLE

###Canvas_Observer
- [ ] ONDONE*
- [ ] ONINIT*
- [ ] ONINITIALUPDATE*
- [ ] ONINITIALUPDATED*
- [ ] ONSIGNAL*
- [ ] ONUPDATE*
- [ ] ONUPDATED*
- [ ] ONWINDOWFOCUSOFF
- [ ] ONWINDOWFOCUSON

###Canvasobserver
- [x] ONWINDOWFOCUSOFF
- [x] ONWINDOWFOCUSON

###Class
- [x] BASE
- [x] DEF

###ComplexCondition
- [x] CONDITION1
- [x] CONDITION2
- [x] DESCRIPTION
- [x] ONRUNTIMEFAILED
- [x] ONRUNTIMESUCCESS
- [x] OPERATOR

###ComplexCondition*
- [ ] CONDITION1
- [ ] CONDITION2
- [ ] DESCRIPTION
- [ ] ONRUNTIMEFAILED
- [ ] ONRUNTIMESUCCESS
- [ ] OPERATOR

###Condition
- [x] DESCRIPTION
- [x] ONRUNTIMEFAILED
- [x] ONRUNTIMESUCCESS
- [x] OPERAND1
- [x] OPERAND2
- [x] OPERATOR

###Database
- [x] MODEL
- [ ] ONDONE*
- [x] ONINIT
- [ ] ONSIGNAL*

###Dialog*
- [ ] ONDONE*
- [ ] ONINIT*
- [ ] ONSIGNAL*

###Double
- [ ] ONBRUTALCHANGED*
- [ ] ONCHANGED*
- [ ] ONDONE*
- [ ] ONINIT*
- [ ] ONNETCHANGED*
- [ ] ONSIGNAL*
- [x] TOINI
- [x] VALUE

###Editbox*
- [ ] ONCHANGED*
- [ ] ONDONE*
- [ ] ONENTER*
- [ ] ONESC*
- [ ] ONFOCUSOFF*
- [ ] ONFOCUSON*
- [ ] ONINIT*
- [ ] ONSIGNAL*
- [ ] PRIORITY*
- [ ] RECT*
- [ ] TEXT*

###Episode
- [x] AUTHOR
- [x] CREATIONTIME
- [x] DESCRIPTION
- [x] LASTMODIFYTIME
- [x] PATH
- [x] SCENES
- [x] STARTWITH
- [x] VERSION

###Expression
- [x] OPERAND1
- [x] OPERAND2
- [x] OPERATOR

###Filter
- [x] ACTION

###Font
- [ ] DEF_[family]_[style]_[size]
- [ ] ONDONE*
- [ ] ONINIT*
- [ ] ONSIGNAL*

###GRBuffer*
- [ ] ONDONE*
- [ ] ONINIT*
- [ ] ONSIGNAL*
- [ ] SOURCE*

###Group
- [ ] ONDONE*
- [x] ONINIT
- [ ] ONSIGNAL*

###Image
- [x] DESCRIPTION
- [x] FILENAME
- [x] MONITORCOLLISION
- [x] MONITORCOLLISIONALPHA
- [x] ONCLICK
- [ ] ONCOLLISION*
- [ ] ONCOLLISIONFINISHED*
- [ ] ONDONE*
- [x] ONFOCUSOFF
- [x] ONFOCUSON
- [x] ONINIT
- [ ] ONRELEASE*
- [ ] ONSIGNAL*
- [x] PRELOAD
- [x] PRIORITY
- [x] RELEASE
- [x] TOCANVAS
- [x] VISIBLE

###Integer
- [x] DESCRIPTION
- [x] ONBRUTALCHANGED
- [x] ONCHANGED
- [ ] ONDONE*
- [x] ONINIT
- [ ] ONNETCHANGED*
- [x] ONSIGNAL
- [x] TOINI
- [x] VALUE
- [x] VARTYPE

###Internet*
- [ ] ONCONNECTED*
- [ ] ONDONE*
- [ ] ONDOWNLOADED*
- [ ] ONERROR*
- [ ] ONINIT*
- [ ] ONSIGNAL*
- [ ] SERVER*
- [ ] VARSERVER*

###Joystick*
- [ ] ONBUTTONDOWN*
- [ ] ONBUTTONUP*
- [ ] ONDONE*
- [ ] ONINIT*
- [ ] ONMOVE*
- [ ] ONSIGNAL*
- [ ] PORT*

###Keyboard
- [x] ONCHAR
- [ ] ONDONE*
- [ ] ONINIT*
- [x] ONKEYDOWN
- [x] ONKEYUP
- [ ] ONSIGNAL*

###Matrix
- [x] BASEPOS
- [x] CELLHEIGHT
- [x] CELLWIDTH
- [x] ONLATEST
- [x] ONNEXT
- [x] SIZE

###Mouse
- [x] ONCLICK
- [x] ONDBLCLICK
- [ ] ONDONE*
- [x] ONINIT
- [x] ONMOVE
- [x] ONRELEASE
- [ ] ONSIGNAL*
- [x] RAW

###MultiArray
- [x] DIMENSIONS

###Music
- [x] FILENAME

###NetClient*
- [ ] NETCLIENT*
- [ ] ONCOMMAND*
- [ ] ONCONNECTED*
- [ ] ONCONNECTIONCLOSED*

###NetServer*
- [ ] NETSERVER*
- [ ] ONCOMMAND*
- [ ] ONDESTROYPLAYER*
- [ ] ONNEWPLAYER*
- [ ] ONTEXT*

###Pattern
- [x] GRIDX
- [x] GRIDY
- [x] HEIGHT
- [x] LAYERS
- [ ] ONCLICK*
- [ ] ONDONE*
- [ ] ONFOCUSOFF*
- [ ] ONFOCUSON*
- [ ] ONINIT*
- [ ] ONRELEASE*
- [ ] ONSIGNAL*
- [x] PRIORITY
- [x] TOCANVAS
- [x] VISIBLE
- [x] WIDTH

###Scene
- [x] AUTHOR
- [x] BACKGROUND
- [ ] COAUTHORS*
- [x] CREATIONTIME
- [ ] DEAMON*
- [x] DESCRIPTION
- [x] DLLS
- [x] LASTMODIFYTIME
- [x] MUSIC
- [ ] ONACTIVATE*
- [ ] ONDEACTIVATE*
- [ ] ONDOMODAL*
- [ ] ONDONE*
- [ ] ONINIT*
- [ ] ONMUSICLOOPED*
- [ ] ONRESTART*
- [ ] ONSIGNAL*
- [x] PATH
- [x] VERSION

###Scroll*
- [ ] ONBOTTOM*
- [ ] ONDONE*
- [ ] ONINIT*
- [ ] ONLEFT*
- [ ] ONRIGHT*
- [ ] ONSIGNAL*
- [ ] ONTOP*
- [ ] VIEWPORT*

###Sequence
- [x] FILENAME
- [ ] ONDONE*
- [x] ONFINISHED
- [x] ONINIT
- [ ] ONSIGNAL*
- [x] ONSTARTED
- [x] VISIBLE

###Sound
- [x] DESCRIPTION
- [x] FILENAME
- [x] FLUSHAFTERPLAYED
- [ ] ONDONE*
- [x] ONFINISHED
- [x] ONINIT
- [ ] ONRESUMED*
- [ ] ONSIGNAL*
- [x] ONSTARTED
- [x] PRELOAD
- [x] RELEASE

###StaticFilter
- [x] ACTION

###String
- [x] DESCRIPTION
- [x] ONBRUTALCHANGED
- [x] ONCHANGED
- [ ] ONDONE*
- [x] ONINIT
- [ ] ONNETCHANGED*
- [ ] ONSIGNAL*
- [x] TOINI
- [x] VALUE

###Struct
- [x] FIELDS
- [ ] ONDONE*
- [ ] ONINIT*
- [ ] ONSIGNAL*

###Text
- [x] FONT
- [x] HJUSTIFY
- [ ] HYPERTEXT*
- [x] MONITORCOLLISION
- [x] MONITORCOLLISIONALPHA
- [ ] ONCOLLISION*
- [ ] ONCOLLISIONFINISHED*
- [ ] ONDONE*
- [x] ONINIT
- [ ] ONSIGNAL*
- [x] PRIORITY
- [x] RECT
- [x] TEXT
- [x] TOCANVAS
- [x] VISIBLE
- [x] VJUSTIFY

###Timer
- [x] ELAPSE
- [x] ENABLED
- [ ] ONDONE*
- [x] ONINIT
- [ ] ONSIGNAL*
- [x] ONTICK
- [x] TICKS

###Vector
- [x] SIZE
- [x] VALUE

###VirtualGraphicsObject*
- [ ] ASBUTTON*
- [ ] MASK*
- [ ] MONITORCOLLISION*
- [ ] MONITORCOLLISIONALPHA*
- [ ] ONDONE*
- [ ] ONINIT*
- [ ] ONSIGNAL*
- [ ] PRIORITY*
- [ ] SOURCE*
- [ ] TOCANVAS*
- [ ] VISIBLE*

###World
- [x] FILENAME
