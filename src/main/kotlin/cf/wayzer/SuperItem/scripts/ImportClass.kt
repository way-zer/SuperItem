package cf.wayzer.SuperItem.scripts

@Target(AnnotationTarget.FILE)
@Repeatable
annotation class ImportClass(
        val name:String
)