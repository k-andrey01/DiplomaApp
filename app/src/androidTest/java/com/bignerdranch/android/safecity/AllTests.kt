import com.bignerdranch.android.safecity.AddCrimeTest
import com.bignerdranch.android.safecity.EditPassTest
import com.bignerdranch.android.safecity.LoginTest
import com.bignerdranch.android.safecity.RegTest
import org.junit.runner.RunWith
import org.junit.runners.Suite

@RunWith(Suite::class)
@Suite.SuiteClasses(
    LoginTest::class,
    EditPassTest::class,
    RegTest::class,
    AddCrimeTest::class
)
class AllTests