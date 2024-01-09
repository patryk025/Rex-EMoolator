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
- [x] INERTIA
- [x] INTEGER
- [ ] INTERNET
- [ ] JOYSTICK
- [x] KEYBOARD
- [ ] LIFO
- [x] MATRIX
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
- [x] SIMPLE (jedynie używane w plikach sekwencji)
- [ ] SIGNAL
- [x] SOUND
- [x] SPEAKING (jedynie używane w plikach sekwencji)
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
- [x] @BREAK (na razie tylko jako kod operacji)
- [x] @CONTINUE (na razie tylko jako kod operacji)
- [x] @CONV
- [ ] @CREATE
- [x] @DOUBLE
- [x] @FOR
- [x] @GETAPPLICATIONNAME (na razie placeholdery)
- [x] @GETCURRENTSCENE
- [x] @IF
- [x] @INT
- [x] @LOOP
- [ ] @MSGBOX
- [x] @ONEBREAK (na razie tylko jako kod operacji)
- [x] @RETURN (na razie tylko jako kod operacji)
- [ ] @RUNONTIMER
- [x] @STRING
- [ ] @VALUE
- [x] @WHILE

###Disclaimer: pola, sygnały bądź typy z gwiazdką na końcu to te, których automatyczny skaner nie znalazł ani razu w skryptach gier z Reksiem (lista w trakcie tworzenia)
W oparciu o arkusz pod linkiem https://docs.google.com/spreadsheets/d/132gkNJ0_GsZXAX17Jlp9qo1DMSRpbPdLWJpIBTCpvhI/edit?usp=drivesdk oraz własne badania

###Animo
- [ ] ASBUTTON
- [x] DESCRIPTION
- [x] FILENAME
- [ ] FLUSHAFTERPLAYED
- [x] FPS
- [x] MONITORCOLLISION
- [x] MONITORCOLLISIONALPHA
- [x] PRELOAD
- [x] PRIORITY
- [x] RELEASE
- [x] TOCANVAS
- [x] VISIBLE
- [ ] CLEARCLIPPING
- [ ] DRAWONTO
- [ ] FLIPH
- [ ] FLIPV
- [ ] GETALPHA
- [ ] GETANCHOR
- [ ] GETCENTERX
- [ ] GETCENTERY
- [ ] GETCFRAMEINEVENT
- [ ] GETCURRFRAMEPOSX
- [ ] GETCURRFRAMEPOSY
- [ ] GETENDX
- [ ] GETENDY
- [ ] GETEVENTNAME
- [ ] GETEVENTNUMBER
- [ ] GETFPS
- [ ] GETFRAME
- [ ] GETFRAMENAME
- [ ] GETFRAMENO
- [ ] GETHEIGHT
- [ ] GETMAXHEIGHT
- [ ] GETMAXWIDTH
- [ ] GETNOE
- [ ] GETNOF
- [ ] GETNOFINEVENT
- [ ] GETOPACITY
- [ ] GETPIXEL
- [ ] GETPOSITIONX
- [ ] GETPOSITIONY
- [ ] GETPRIORITY
- [ ] GETWIDTH
- [ ] HIDE
- [ ] INVALIDATE
- [ ] ISAT
- [ ] ISINSIDE
- [ ] ISNEAR
- [ ] ISPLAYING
- [ ] ISVISIBLE
- [ ] LOAD
- [ ] MERGEALPHA
- [ ] MONITORCOLLISION
- [ ] MOVE
- [ ] NEXTFRAME
- [ ] NPLAY
- [ ] PAUSE
- [ ] PLAY
- [ ] PLAYRAND
- [ ] PLAYREVERSE
- [ ] PREVFRAME
- [ ] REMOVEMONITORCOLLISION
- [ ] REPLACECOLOR
- [ ] RESETFLIPS
- [ ] RESUME
- [ ] SETANCHOR
- [ ] SETASBUTTON
- [ ] SETBACKWARD
- [ ] SETCLIPPING
- [ ] SETFORWARD
- [ ] SETFPS
- [ ] SETFRAME
- [ ] SETFRAMENAME
- [ ] SETFREQ
- [ ] SETONFF
- [ ] SETOPACITY
- [ ] SETPOSITION
- [ ] SETPRIORITY
- [ ] SETPAN
- [ ] SETVOLUME
- [ ] SHOW
- [ ] STOP
- [ ] ONCLICK
- [ ] ONCOLLISION
- [ ] ONCOLLISIONFINISHED
- [ ] ONDONE
- [ ] ONFINISHED
- [ ] ONFIRSTFRAME
- [ ] ONFOCUSOFF
- [ ] ONFOCUSON
- [ ] ONFRAMECHANGED
- [ ] ONINIT
- [ ] ONPAUSED
- [ ] ONRELEASE
- [ ] ONRESUMED
- [ ] ONSIGNAL
- [ ] ONSTARTED
- [ ] TOP

###Application
- [x] AUTHOR
- [x] BLOOMOO_VERSION
- [x] CREATIONTIME
- [x] DESCRIPTION
- [x] EPISODES
- [x] LASTMODIFYTIME
- [x] PATH
- [ ] SCENES
- [x] STARTWITH
- [x] VERSION
- [ ] DISABLEMUSIC
- [ ] ENABLEMUSIC
- [ ] EXISTSENV
- [ ] EXIT
- [ ] GETLANGUAGE
- [ ] GETPLAYER
- [ ] GOTO
- [ ] PRINT
- [ ] RELOAD
- [ ] RESTART
- [ ] RUN
- [ ] RUNENV
- [ ] SETLANGUAGE
- [ ] STARTDRAGGINGWINDOW
- [ ] STOPDRAGGINGWINDOW
- [ ] STOREBINARY

###Array
- [x] DESCRIPTION
- [ ] SENDONCHANGE
- [ ] ADD
- [ ] ADDAT
- [ ] ADDCLONES
- [ ] CHANGEAT
- [ ] CLAMPAT
- [ ] COMPARE
- [ ] CONTAINS
- [ ] COPYTO
- [ ] DIR
- [ ] DIV
- [ ] DIVA
- [ ] DIVAT
- [ ] FILL
- [ ] FIND
- [ ] FINDALL
- [ ] GET
- [ ] GETMARKERPOS
- [ ] GETSIZE
- [ ] GETSUMVALUE
- [ ] INSERTAT
- [ ] LOAD
- [ ] LOADINI
- [ ] MAX
- [ ] MAXD
- [ ] MIN
- [ ] MIND
- [ ] MODAT
- [ ] MUL
- [ ] MULA
- [ ] MULAT
- [ ] NEXT
- [ ] PREV
- [ ] RANDOMFILL
- [ ] REMOVE
- [ ] REMOVEALL
- [ ] REMOVEAT
- [ ] RESETMARKER
- [ ] REVERSEFIND
- [ ] ROTATELEFT
- [ ] ROTATERIGHT
- [ ] SAVE
- [ ] SAVEINI
- [ ] SENDONCHANGE
- [ ] SETMARKERPOS
- [ ] SHIFTLEFT
- [ ] SHIFTRIGHT
- [ ] SORT
- [ ] SORTMANY
- [ ] SUB
- [ ] SUBA
- [ ] SUBAT
- [ ] SUM
- [ ] SUMA
- [ ] SWAP
- [ ] ONCHANGE
- [ ] ONDONE
- [ ] ONINIT
- [ ] ONSIGNAL

###Behaviour
- [x] CODE
- [x] CONDITION
- [x] DESCRIPTION
- [ ] BREAK
- [ ] DISABLE
- [ ] RUN
- [ ] RUNC
- [ ] RUNLOOPED
- [ ] ONDONE
- [ ] ONINIT
- [ ] ONSIGNAL

###Bool
- [x] DESCRIPTION
- [ ] DEFAULT
- [ ] NETNOTIFY
- [x] TOINI
- [x] VALUE
- [ ] AND
- [ ] CLEAR
- [ ] COPYFILE
- [ ] DEC
- [ ] GET
- [ ] INC
- [ ] AND
- [ ] OR
- [ ] RANDOM
- [ ] RESETINI
- [ ] SET
- [ ] SETDEFAULT
- [ ] SWITCH
- [ ] OR
- [ ] ONBRUTALCHANGED
- [ ] ONCHANGED
- [ ] ONDONE
- [ ] ONINIT
- [ ] ONNETCHANGED
- [ ] ONSIGNAL

###Button
- [ ] ACCENT
- [x] DESCRIPTION
- [ ] DRAG
- [x] DRAGGABLE
- [x] ENABLE
- [x] GFXONCLICK
- [x] GFXONMOVE
- [x] GFXSTANDARD
- [x] PRIORITY
- [x] RECT
- [ ] SNDONCLICK
- [x] SNDONMOVE
- [ ] SNDSTANDARD
- [ ] ACCENT
- [ ] DISABLE
- [ ] DISABLEBUTVISIBLE
- [ ] DISABLEDRAGGING
- [ ] ENABLE
- [ ] ENABLEDRAGGING
- [ ] GETONCLICK
- [ ] GETONMOVE
- [ ] GETPRIORITY
- [ ] GETSTD
- [ ] SETONCLICK
- [ ] SETONMOVE
- [ ] SETPRIORITY
- [ ] SETRECT
- [ ] SETSTD
- [ ] SYN
- [ ] ONACTION
- [ ] ONCLICKED
- [ ] ONDONE
- [ ] ONDRAGGING
- [ ] ONENDDRAGGING
- [ ] ONFOCUSOFF
- [ ] ONFOCUSON
- [ ] ONINIT
- [ ] ONPAUSED
- [ ] ONRELEASED
- [ ] ONSIGNAL
- [ ] ONSTARTDRAGGING

###Canvas
- [ ] ALPHA
- [ ] BACKGROUND
- [ ] HEIGHT
- [ ] PRIORITY
- [ ] WIDTH
- [ ] ADD
- [ ] CAPTURE
- [ ] CLEAR
- [ ] DRAW
- [ ] ERASE
- [ ] GETHEIGHT
- [ ] GETNUMVISIBLEPIXELS
- [ ] GETPOSITIONX
- [ ] GETPOSITIONY
- [ ] GETWIDTH
- [ ] HIDE
- [ ] MOVE
- [ ] RECREATE
- [ ] REMOVE
- [ ] SAVE
- [ ] SETASBUTTON
- [ ] SETOPACITY
- [ ] SETPOSITION
- [ ] SETPRIORITY
- [ ] SHOW
- [ ] ONCLICK
- [ ] ONDONE
- [ ] ONFOCUSOFF
- [ ] ONFOCUSON
- [ ] ONINIT
- [ ] ONRELEASE
- [ ] ONSIGNAL

###Canvas_observer
- [ ] ADD
- [ ] ENABLENOTIFY
- [ ] GETBPP
- [ ] GETGRAPHICSAT
- [ ] GETGRAPHICSAT2
- [ ] MOVEBKG
- [ ] PASTE
- [ ] REDRAW
- [ ] REFRESH
- [ ] REMOVE
- [ ] SAVE
- [ ] SETBACKGROUND
- [ ] SETBKGPOS
- [ ] ONDONE
- [ ] ONINIT
- [ ] ONINITIALUPDATE
- [ ] ONINITIALUPDATED
- [ ] ONSIGNAL
- [ ] ONUPDATE
- [ ] ONUPDATED
- [ ] ONWINDOWFOCUSOFF
- [ ] ONWINDOWFOCUSON

###Class
- [x] BASE
- [x] DEF
- [ ] DELETE
- [ ] NEW

###CNVLoader
- [ ] CNVLOADER
- [ ] LOAD
- [ ] RELEASE

###Condition
- [x] DESCRIPTION
- [x] OPERAND1
- [x] OPERAND2
- [x] OPERATOR
- [ ] BREAK
- [ ] CHECK
- [ ] ONE_BREAK
- [ ] ONRUNTIMEFAILED
- [ ] ONRUNTIMESUCCESS

###ComplexCondition
- [x] CONDITION1
- [x] CONDITION2
- [x] DESCRIPTION
- [x] OPERATOR
- [ ] BREAK
- [ ] CHECK
- [ ] ONE_BREAK
- [ ] ONRUNTIMEFAILED
- [ ] ONRUNTIMESUCCESS

###Database
- [x] MODEL
- [ ] ADD
- [ ] FIND
- [ ] GETCURSORPOS
- [ ] GETROWSNO
- [ ] INSERTAT
- [ ] LOAD
- [ ] NEXT
- [ ] PREV
- [ ] REMOVE
- [ ] REMOVEALL
- [ ] REMOVEAT
- [ ] SAVE
- [ ] SELECT
- [ ] ONDONE
- [ ] ONINIT
- [ ] ONSIGNAL

###Dialog
- [ ] ADD
- [ ] HIDE
- [ ] SETBACKGROUND
- [ ] SHOW
- [ ] ONDONE
- [ ] ONINIT
- [ ] ONSIGNAL

###Double
- [ ] DEFAULT
- [ ] NETNOTIFY
- [x] TOINI
- [x] VALUE
- [ ] ADD
- [ ] ARCTAN
- [ ] ARCTANEX
- [ ] CLAMP
- [ ] CLEAR
- [ ] COPYFILE
- [ ] COSINUS
- [ ] DEC
- [ ] DIV
- [ ] GET
- [ ] INC
- [ ] LENGTH
- [ ] LOG
- [ ] MAXA
- [ ] MINA
- [ ] MOD
- [ ] MUL
- [ ] POWER
- [ ] RANDOM
- [ ] RESETINI
- [ ] ROUND
- [ ] SET
- [ ] SETDEFAULT
- [ ] SGN
- [ ] SINUS
- [ ] SQRT
- [ ] SUB
- [ ] SWITCH
- [ ] ONBRUTALCHANGED
- [ ] ONCHANGED
- [ ] ONDONE
- [ ] ONINIT
- [ ] ONNETCHANGED
- [ ] ONSIGNAL

###Editbox
- [ ] CURSOR
- [ ] FONT
- [ ] PRIORITY
- [ ] RECT
- [ ] TEXT
- [ ] ACTIVE
- [ ] ADDTEXT
- [ ] DEACTIVE
- [ ] DISABLE
- [ ] ENABLE
- [ ] ENABLENL
- [ ] GETTEXT
- [ ] MOVE
- [ ] SETCOLOR
- [ ] SETCURSORPOS
- [ ] SETPOSITION
- [ ] SETPRIORITY
- [ ] SETREADONLY
- [ ] SETRECT
- [ ] SETTEXT
- [ ] ONCHANGED
- [ ] ONDONE
- [ ] ONENTER
- [ ] ONESC
- [ ] ONFOCUSOFF
- [ ] ONFOCUSON
- [ ] ONINIT
- [ ] ONSIGNAL

###EditGroup

###Episode
- [x] AUTHOR
- [x] CREATIONTIME
- [x] DESCRIPTION
- [x] LASTMODIFYTIME
- [x] PATH
- [x] SCENES
- [x] STARTWITH
- [ ] BACK
- [ ] GETCURRENTSCENE
- [ ] GETLATESTSCENE
- [ ] GOTO
- [ ] NEXT
- [ ] PREV
- [ ] RESTART

###Expression
- [x] DESCRIPTION
- [x] OPERAND1
- [x] OPERAND2
- [x] OPERATOR

###FIFO

###Filter
- [x] ACTION

###Font
- [x] DEF_[family]_[style]_[size]
- [ ] GETHEIGHT
- [ ] SETCOLOR
- [ ] SETFAMILY
- [ ] SETSIZE
- [ ] SETSTYLE
- [ ] ONDONE
- [ ] ONINIT
- [ ] ONSIGNAL

###GRBuffer
- [ ] DESTINY
- [ ] SOURCE
- [ ] CREATE
- [ ] DRAW
- [ ] FINDAT
- [ ] FLUSH
- [ ] FLUSHALL
- [ ] UNDO
- [ ] ONDONE
- [ ] ONINIT
- [ ] ONSIGNAL

###Group
- [ ] ADD
- [ ] ADDCLONES
- [ ] CLONE
- [ ] CONTAINS
- [ ] GETCLONEINDEX
- [ ] GETMARKERPOS
- [ ] GETNAME
- [ ] GETNAMEATMARKER
- [ ] GETSIZE
- [ ] NEXT
- [ ] PREV
- [ ] REMOVE
- [ ] REMOVEALL
- [ ] RESETMARKER
- [ ] SETMARKERPOS
- [ ] ONDONE
- [ ] ONINIT
- [ ] ONSIGNAL

###Image
- [ ] ASBUTTON
- [x] DESCRIPTION
- [x] FILENAME
- [ ] FLUSHAFTERPLAYER
- [x] MONITORCOLLISION
- [x] MONITORCOLLISIONALPHA
- [x] PRELOAD
- [x] PRIORITY
- [x] RELEASE
- [x] TOCANVAS
- [x] VISIBLE
- [ ] CLEARCLIPPING
- [ ] DRAWONTO
- [ ] FLIPH
- [ ] FLIPV
- [ ] GETALPHA
- [ ] GETCENTERX
- [ ] GETCENTERY
- [ ] GETCOLORAT
- [ ] GETCOLORBAT
- [ ] GETCOLORGAT
- [ ] GETCOLORRAT
- [ ] GETHEIGHT
- [ ] GETOPACITY
- [ ] GETPIXEL
- [ ] GETPOSITIONX
- [ ] GETPOSITIONY
- [ ] GETPRIORITY
- [ ] GETSLIDECOMPS
- [ ] GETWIDTH
- [ ] HIDE
- [ ] INVALIDATE
- [ ] ISAT
- [ ] ISINSIDE
- [ ] ISNEAR
- [ ] ISVISIBLE
- [ ] LINK
- [ ] LOAD
- [ ] MERGEALPHA
- [ ] MERGEALPHA2
- [ ] MONITORCOLLISION
- [ ] MOVE
- [ ] REMOVEMONITORCOLLISION
- [ ] REPLACECOLOR
- [ ] RESETFLIPS
- [ ] RESETPOSITION
- [ ] SAVE
- [ ] SETANCHOR
- [ ] SETASBUTTON
- [ ] SETCLIPPING
- [ ] SETOPACITY
- [ ] SETPOSITION
- [ ] SETPRIORITY
- [ ] SETRESETPOSITION
- [ ] SETSCALEFACTOR
- [ ] SHOW
- [ ] ONCLICK
- [ ] ONCOLLISION
- [ ] ONCOLLISIONFINISHED
- [ ] ONDONE
- [ ] ONFOCUSOFF
- [ ] ONFOCUSON
- [ ] ONINIT
- [ ] ONRELEASE
- [ ] ONSIGNAL

###Inertia

###Integer
- [x] DESCRIPTION
- [ ] DEFAULT
- [ ] NETNOTIFY
- [x] TOINI
- [x] VALUE
- [ ] ABS
- [ ] ADD
- [ ] AND
- [ ] CLAMP
- [ ] CLEAR
- [ ] COPYFILE
- [ ] DEC
- [ ] DIV
- [ ] GET
- [ ] INC
- [ ] MOD
- [ ] MUL
- [ ] NOT
- [ ] OR
- [ ] POWER
- [ ] RANDOM
- [ ] RESETINI
- [ ] SET
- [ ] SETDEFAULT
- [ ] SUB
- [ ] SWITCH
- [ ] XOR
- [ ] ONBRUTALCHANGED
- [ ] ONCHANGED
- [ ] ONDONE
- [ ] ONINIT
- [ ] ONNETCHANGED
- [ ] ONSIGNAL

###Internet
- [ ] SERVER
- [ ] VARSERVER
- [ ] EXTRACT
- [ ] GET
- [ ] GETPROGRESS
- [ ] ONCONNECTED
- [ ] ONDONE
- [ ] ONDOWNLOADED
- [ ] ONERROR
- [ ] ONINIT
- [ ] ONSIGNAL

###Joystick
- [ ] PORT
- [ ] GETBUTTONSTATE
- [ ] GETPOSITION
- [ ] ISLEFT
- [ ] ISRIGHT
- [ ] ONBUTTONDOWN
- [ ] ONBUTTONUP
- [ ] ONDONE
- [ ] ONINIT
- [ ] ONMOVE
- [ ] ONSIGNAL

###Keyboard
- [ ] KEYBOARD
- [ ] DISABLE
- [ ] ENABLE
- [ ] GETLATESTKEY
- [ ] GETLATESTKEYS
- [ ] ISENABLED
- [ ] ISKEYDOWN
- [ ] SETAUTOREPEAT
- [ ] ONCHAR
- [ ] ONDONE
- [ ] ONINIT
- [ ] ONKEYDOWN
- [ ] ONKEYUP
- [ ] ONSIGNAL

###LIFO

###Matrix
- [x] SIZE
- [x] BASEPOS
- [x] CELLHEIGHT
- [x] CELLWIDTH

###Mouse
- [ ] MOUSE
- [ ] CLICK
- [ ] DISABLE
- [ ] DISABLESIGNAL
- [ ] ENABLE
- [ ] ENABLESIGNAL
- [ ] GETLASTCLICKPOSX
- [ ] GETLASTCLICKPOSY
- [ ] GETPOSX
- [ ] GETPOSY
- [ ] HIDE
- [ ] ISLBUTTONDOWN
- [ ] ISRBUTTONDOWN
- [ ] LOCKACTIVECURSOR
- [ ] MOUSERELEASE
- [ ] MOVE
- [x] RAW
- [ ] SET
- [ ] SETACTIVERECT
- [ ] SETCLIPRECT
- [ ] SETPOSITION
- [ ] SHOW
- [ ] ONCLICK
- [ ] ONDBLCLICK
- [ ] ONDONE
- [ ] ONINIT
- [ ] ONMOVE
- [ ] ONRELEASE
- [ ] ONSIGNAL

###Movie

###MultiArray
- [x] DIMENSIONS
- [ ] COUNT
- [ ] LOAD
- [ ] GET
- [ ] GETSIZE
- [ ] SAFEGET
- [ ] SAVE
- [ ] SET

###Music
- [x] FILENAME

###Netclient
- [ ] NETCLIENT
- [ ] CONNECT
- [ ] DISCONNECT
- [ ] ISCONNECTED
- [ ] ONCOMMAND
- [ ] ONCONNECTED
- [ ] ONCONNECTIONCLOSED

###Netpeer
- [ ] GETLATESTTEXT
- [ ] SENDBEHAVIOUR
- [ ] SENDCMD
- [ ] SENDTEXT
- [ ] SENDVARIABLE

###Netserver
- [ ] NETSERVER
- [ ] GETLOGGEDPLAYERNAME
- [ ] GETPLAYERNAME
- [ ] GETPLAYERSNO
- [ ] ISSTARTED
- [ ] START
- [ ] STOP
- [ ] ONCOMMAND
- [ ] ONDESTROYPLAYER
- [ ] ONNEWPLAYER
- [ ] ONTEXT

###Painter

###Path (z dll?)

###Pattern
- [x] GRIDX
- [x] GRIDY
- [x] HEIGHT
- [x] LAYERS
- [x] PRIORITY
- [x] TOCANVAS
- [x] VISIBLE
- [x] WIDTH
- [ ] ADD
- [ ] GETALIASAT
- [ ] GETALIASATCELL
- [ ] GETALIASINFO
- [ ] GETCELLCORD
- [ ] GETGRAPHICSAT
- [ ] GETINFOAT
- [ ] MOVE
- [ ] REMOVE
- [ ] SETGRID
- [ ] SETPOSITION
- [ ] ONCLICK
- [ ] ONDONE
- [ ] ONFOCUSOFF
- [ ] ONFOCUSON
- [ ] ONINIT
- [ ] ONRELEASE
- [ ] ONSIGNAL

###Rand
- [ ] GET
- [ ] GETPLENTY

###Scene
- [x] AUTHOR
- [x] BACKGROUND
- [ ] COAUTHORS
- [x] CREATIONTIME
- [ ] DEAMON
- [x] DESCRIPTION
- [x] DLLS
- [x] LASTMODIFYTIME
- [x] MUSIC
- [x] PATH
- [ ] CREATEOBJECT
- [ ] GETDRAGGEDNAME
- [ ] GETELEMENTSNO
- [ ] GETMAXHSPRIORITY
- [ ] GETMINHSPRIORITY
- [ ] GETMUSICVOLUME
- [ ] GETOBJECTS
- [ ] GETPLAYINGANIMO
- [ ] GETPLAYINGSEQ
- [ ] GETRUNNINGTIMER
- [ ] ISPAUSED
- [ ] PAUSE
- [ ] REMOVE
- [ ] REMOVECLONES
- [ ] RESUME
- [ ] RESUMEONLY
- [ ] RESUMESEQONLY
- [ ] RUN
- [ ] RUNCLONES
- [ ] SETMAXHSPRIORITY
- [ ] SETMINHSPRIORITY
- [ ] SETMUSICFREQ
- [ ] SETMUSICPAN
- [ ] SETMUSICVOLUME
- [ ] STARTMUSIC
- [ ] STOPMUSIC
- [ ] TOTIME
- [ ] ONACTIVATE
- [ ] ONDEACTIVATE
- [ ] ONDOMODAL
- [ ] ONDONE
- [ ] ONINIT
- [ ] ONMUSICLOOPED
- [ ] ONRESTART
- [ ] ONSIGNAL

###Scroll
- [ ] VIEWPORT
- [ ] ADD
- [ ] INVALIDATE
- [ ] MOVE
- [ ] MOVEVIEWPORT
- [ ] REMOVE
- [ ] SCROLLDOWN
- [ ] SCROLLLEFT
- [ ] SCROLLRIGHT
- [ ] SCROLLUP
- [ ] SETVIEWPORT
- [ ] ONBOTTOM
- [ ] ONDONE
- [ ] ONINIT
- [ ] ONLEFT
- [ ] ONRIGHT
- [ ] ONSIGNAL
- [ ] ONTOP

###Sequence
- [x] DESCRIPTION
- [x] FILENAME
- [ ] GETEVENTNAME
- [ ] GETPLAYING
- [ ] HIDE
- [ ] ISPLAYING
- [ ] PAUSE
- [ ] PLAY
- [ ] RESUME
- [ ] SETFREQ
- [ ] SETPAN
- [ ] SETVOLUME
- [x] SEQEVENT
- [ ] SHOW
- [ ] STOP
- [x] VISIBLE
- [ ] ONDONE
- [ ] ONFINISHED
- [ ] ONINIT
- [ ] ONSIGNAL
- [ ] ONSTARTED

###Signal

###Simple
- [x] FILENAME
- [x] EVENT

###Sound
- [x] DESCRIPTION
- [x] FILENAME
- [x] FLUSHAFTERPLAYED
- [x] PRELOAD
- [ ] ISPLAYING
- [ ] LOAD
- [ ] PAUSE
- [ ] PLAY
- [x] RELEASE
- [ ] RESUME
- [ ] SETFREQ
- [ ] SETPAN
- [ ] SETVOLUME
- [ ] STOP
- [ ] ONDONE
- [ ] ONFINISHED
- [ ] ONINIT
- [ ] ONRESUMED
- [ ] ONSIGNAL
- [ ] ONSTARTED

###StaticFilter
- [x] ACTION
- [ ] LINK
- [ ] UNLINK
- [ ] SETPROPERTY

###String
- [ ] DEFAULT
- [x] DESCRIPTION
- [ ] NETNOTIFY
- [x] TOINI
- [x] VALUE
- [ ] ADD
- [ ] CLEAR
- [ ] COPYFILE
- [ ] CUT
- [ ] FIND
- [ ] GET
- [ ] INSERTAT
- [ ] ISUPPERLETTER
- [ ] LENGTH
- [ ] LOWER
- [ ] NOT
- [ ] RANDOM
- [ ] REPLACE
- [ ] REPLACEAT
- [ ] RESETINI
- [ ] SET
- [ ] SETDEFAULT
- [ ] SUB
- [ ] SWITCH
- [ ] UPPER
- [ ] ONBRUTALCHANGED
- [ ] ONCHANGED
- [ ] ONDONE
- [ ] ONINIT
- [ ] ONNETCHANGED
- [ ] ONSIGNAL

###Struct
- [x] FIELDS
- [ ] GETFIELD
- [ ] SET
- [ ] SETFIELD
- [ ] ONDONE
- [ ] ONINIT
- [ ] ONSIGNAL

###System
- [ ] SYSTEM
- [ ] COPYFILE
- [ ] CREATEDIR
- [ ] DELAY
- [ ] GETCMDLINEPARAMETER
- [ ] GETCOMMANDLINE
- [ ] GETDATE
- [ ] GETDATESTRING
- [ ] GETDAY
- [ ] GETDAYOFWEEK
- [ ] GETDAYOFWEEKSTRING
- [ ] GETFOLDERLOCATION
- [ ] GETHOUR
- [ ] GETMHZ
- [ ] GETMINUTES
- [ ] GETMONTH
- [ ] GETMONTHSTRING
- [ ] GETSECONDS
- [ ] GETSYSTEMTIME
- [ ] GETTIMESTRING
- [ ] GETUSERNAME
- [ ] GETYEAR
- [ ] INSTALL
- [ ] ISCMDLINEPARAMETER
- [ ] ISFILEEXIST
- [ ] MINIMIZE
- [ ] UNINSTALL

###Text
- [x] FONT
- [x] HJUSTIFY
- [ ] HYPERTEXT
- [x] MONITORCOLLISION
- [x] MONITORCOLLISIONALPHA
- [x] PRIORITY
- [x] RECT
- [x] TEXT
- [x] TOCANVAS
- [x] VISIBLE
- [x] VJUSTIFY
- [ ] CLEARCLIPPING
- [ ] DRAWONTO
- [ ] GETHEIGHT
- [ ] GETNUMWORDS
- [ ] GETPOSITIONX
- [ ] GETPOSITIONY
- [ ] GETWIDTH
- [ ] GETWORDAT
- [ ] GETWORDATXY
- [ ] GETWORDPOSX
- [ ] GETWORDPOSY
- [ ] GETWORDWIDTH
- [ ] HIDE
- [ ] INVALIDATE
- [ ] ISNEAR
- [ ] LOAD
- [ ] MOVE
- [ ] SEARCH
- [ ] SETCLIPPING
- [ ] SETCOLOR
- [ ] SETFONT
- [ ] SETJUSTIFY
- [ ] SETOPACITY
- [ ] SETPOSITION
- [ ] SETPRIORITY
- [ ] SETRECT
- [ ] SETTEXT
- [ ] SETTEXTDOUBLE
- [ ] SETWORDCOLOR
- [ ] SHOW
- [ ] ONCOLLISION
- [ ] ONCOLLISIONFINISHED
- [ ] ONDONE
- [ ] ONINIT
- [ ] ONSIGNAL

###Timer
- [x] ELAPSE
- [x] ENABLED
- [x] TICKS
- [ ] DISABLE
- [ ] ENABLE
- [ ] GETTICKS
- [ ] PAUSE
- [ ] RESET
- [ ] RESUME
- [ ] SET
- [ ] SETELAPSE
- [ ] ONDONE
- [ ] ONINIT
- [ ] ONSIGNAL
- [ ] ONTICK

###Vector
- [x] SIZE
- [x] VALUE

###VirtualGraphicsObject
- [ ] ASBUTTON
- [ ] MASK
- [ ] MONITORCOLLISION
- [ ] MONITORCOLLISIONALPHA
- [ ] PRIORITY
- [ ] SOURCE
- [ ] TOCANVAS
- [ ] VISIBLE
- [ ] CLEARCLIPPING
- [ ] GETHEIGHT
- [ ] GETPOSITIONX
- [ ] GETPOSITIONY
- [ ] GETWIDTH
- [ ] HIDE
- [ ] INVALIDATE
- [ ] ISINSIDE
- [ ] ISNEAR
- [ ] ISVISIBLE
- [ ] MONITORCOLLISION
- [ ] MOVE
- [ ] REMOVEMONITORCOLLISION
- [ ] SETASBUTTON
- [ ] SETCLIPPING
- [ ] SETMASK
- [ ] SETPOSITION
- [ ] SETPRIORITY
- [ ] SETSOURCE
- [ ] SHOW
- [ ] ONDONE
- [ ] ONINIT
- [ ] ONSIGNAL

###World
- [x] FILENAME
