package com.itartisan.cn.common.core.constant;

/**
 * 返回状态码
 */
public interface HttpStatus {

    /* 这一类型的状态码，代表请求已被接受，需要继续处理。
     * 这类响应是临时响应，只包含状态行和某些可选的响应头信息，并以空行结束。
     * 由于 HTTP/1.0 协议中没有定义任何 1xx 状态码，所以除非在某些试验条件下，服务器禁止向此类客户端发送 1xx 响应。
     */
    /**
     * 客户端应当继续发送请求。客户端应当继续发送请求。这个临时响应是用来通知客户端它的部分请求已经被服务器接收，且仍未被拒绝。
     * 客户端应当继续发送请求的剩余部分，或者如果请求已经完成，忽略这个响应。服务器必须在请求完成后向客户端发送一个最终响应。
     */
    int CONTINUE = 100;

    /**
     * 服务器已经理解了客户端的请求，并将通过Upgrade 消息头通知客户端采用不同的协议来完成这个请求。
     * 在发送完这个响应最后的空行后，服务器将会切换到在Upgrade 消息头中定义的那些协议。
     * 只有在切换新的协议更有好处的时候才应该采取类似措施。
     * 例如，切换到新的HTTP 版本比旧版本更有优势，或者切换到一个实时且同步的协议以传送利用此类特性的资源。
     */
    int SWITCHING_PROTOCOLS = 101;

    /**
     * 由WebDAV（RFC 2518）扩展的状态码，代表处理将被继续执行。
     */
    int PROCESSING = 102;

    /* 这一类型的状态码，代表请求已成功被服务器接收、理解、并接受。*/
    /**
     * 请求已成功，请求所希望的响应头或数据体将随此响应返回。出现此状态码是表示正常状态。
     */
    int SUCCESS = 200;

    /**
     * 请求已经被实现，而且有一个新的资源已经依据请求的需要而建立，且其 URI 已经随Location 头信息返回。
     * 假如需要的资源无法及时建立的话，应当返回 '202 Accepted'。
     */
    int CREATED = 201;

    /**
     * 服务器已接受请求，但尚未处理。
     * 正如它可能被拒绝一样，最终该请求可能会也可能不会被执行。在异步操作的场合下，没有比发送这个状态码更方便的做法了。
     * 返回202状态码的响应的目的是允许服务器接受其他过程的请求（例如某个每天只执行一次的基于批处理的操作），
     * 而不必让客户端一直保持与服务器的连接直到批处理操作全部完成。
     * 在接受请求处理并返回202状态码的响应应当在返回的实体中包含一些指示处理当前状态的信息，以及指向处理状态监视器或状态预测的指针，
     * 以便用户能够估计操作是否已经完成。
     */
    int ACCEPTED = 202;

    /**
     * 服务器已成功处理了请求，但返回的实体头部元信息不是在原始服务器上有效的确定集合，而是来自本地或者第三方的拷贝。
     * 当前的信息可能是原始版本的子集或者超集。例如，包含资源的元数据可能导致原始服务器知道元信息的超集。
     * 使用此状态码不是必须的，而且只有在响应不使用此状态码便会返回200 OK的情况下才是合适的。
     */
    int NON_AUTHORITATIVE_INFORMATION = 203;

    /**
     * 服务器成功处理了请求，但不需要返回任何实体内容，并且希望返回更新了的元信息。
     * 响应可能通过实体头部的形式，返回新的或更新后的元信息。如果存在这些头部信息，则应当与所请求的变量相呼应。
     * <p>
     * 如果客户端是浏览器的话，那么用户浏览器应保留发送了该请求的页面，而不产生任何文档视图上的变化，
     * 即使按照规范新的或更新后的元信息应当被应用到用户浏览器活动视图中的文档。
     * <p>
     * 由于204响应被禁止包含任何消息体，因此它始终以消息头后的第一个空行结尾。
     */
    int NO_CONTENT = 204;

    /**
     * 服务器成功处理了请求，且没有返回任何内容。
     * 但是与204响应不同，返回此状态码的响应要求请求者重置文档视图。
     * 该响应主要是被用于接受用户输入后，立即重置表单，以便用户能够轻松地开始另一次输入。
     * 与204响应一样，该响应也被禁止包含任何消息体，且以消息头后的第一个空行结束。
     */
    int RESET_CONTENT = 205;

    /**
     * 服务器已经成功处理了部分 GET 请求。
     * 类似于 FlashGet 或者迅雷这类的 HTTP下载工具都是使用此类响应实现断点续传或者将一个大文档分解为多个下载段同时下载。
     */
    int PARTIAL_CONTENT = 206;

    /**
     * 由WebDAV(RFC 2518)扩展的状态码，代表之后的消息体将是一个XML消息，并且可能依照之前子请求数量的不同，包含一系列独立的响应代码。
     */
    int MULTI_STATUS = 207;

    /* 这类状态码代表需要客户端采取进一步的操作才能完成请求。通常，这些状态码用来重定向，后续的请求地址（重定向目标）在本次响应的 Location 域中指明。
     * 当且仅当后续的请求所使用的方法是 GET 或者 HEAD 时，用户浏览器才可以在没有用户介入的情况下自动提交所需要的后续请求。
     * 客户端应当自动监测无限循环重定向（例如：A->A，或者A->B->C->A），因为这会导致服务器和客户端大量不必要的资源消耗。
     * 按照 HTTP/1.0 版规范的建议，浏览器不应自动访问超过5次的重定向。
     */

    /**
     * 被请求的资源有一系列可供选择的回馈信息，每个都有自己特定的地址和浏览器驱动的商议信息。用户或浏览器能够自行选择一个首选的地址进行重定向。
     */
    int MULTIPLE_CHOICES = 300;

    /**
     * 被请求的资源已永久移动到新位置，并且将来任何对此资源的引用都应该使用本响应返回的若干个 URI 之一。
     * 如果可能，拥有链接编辑功能的客户端应当自动把请求的地址修改为从服务器反馈回来的地址。除非额外指定，否则这个响应也是可缓存的。
     */
    int MOVED_PERM = 301;

    /**
     * 请求的资源临时从不同的 URI响应请求。由于这样的重定向是临时的，客户端应当继续向原有地址发送以后的请求。
     * 只有在Cache-Control或Expires中进行了指定的情况下，这个响应才是可缓存的。
     */
    int MOVE_TEMPORARILY = 302;

    /**
     * 对应当前请求的响应可以在另一个 URL 上被找到，而且客户端应当采用 GET 的方式访问那个资源。
     * 这个方法的存在主要是为了允许由脚本激活的POST请求输出重定向到一个新的资源。
     * 这个新的 URI 不是原始资源的替代引用。同时，303响应禁止被缓存。当然，第二个请求（重定向）可能被缓存。
     */
    int SEE_OTHER = 303;

    /**
     * 如果客户端发送了一个带条件的 GET 请求且该请求已被允许，而文档的内容（自上次访问以来或者根据请求的条件）并没有改变，则服务器应当返回这个状态码。
     * 304响应禁止包含消息体，因此始终以消息头后的第一个空行结尾。
     */
    int NOT_MODIFIED = 304;

    /**
     * 被请求的资源必须通过指定的代理才能被访问。
     * Location 域中将给出指定的代理所在的 URI 信息，接收者需要重复发送一个单独的请求，通过这个代理才能访问相应资源。
     * 只有原始服务器才能建立305响应。
     */
    int USE_PROXY = 305;

    /**
     * 在最新版的规范中，306状态码已经不再被使用。
     */
    int SWITCH_PROXY = 306;

    /**
     * 请求的资源临时从不同的URI 响应请求。
     */
    int TEMPORARY_REDIRECT = 307;

   /* 这类的状态码代表了客户端看起来可能发生了错误，妨碍了服务器的处理。
    * 除非响应的是一个 HEAD 请求，否则服务器就应该返回一个解释当前错误状况的实体，以及这是临时的还是永久性的状况。
    * 这些状态码适用于任何请求方法。浏览器应当向用户显示任何包含在此类错误响应中的实体内容。
    *
    * 如果错误发生时客户端正在传送数据，那么使用TCP的服务器实现应当仔细确保在关闭客户端与服务器之间的连接之前，客户端已经收到了包含错误信息的数据包。
    * 如果客户端在收到错误信息后继续向服务器发送数据，服务器的TCP栈将向客户端发送一个重置数据包，以清除该客户端所有还未识别的输入缓冲，
    * 以免这些数据被服务器上的应用程序读取并干扰后者。
    */
    /**
     * 1、语义有误，当前请求无法被服务器理解。除非进行修改，否则客户端不应该重复提交这个请求。
     * 2、请求参数有误。
     */
    int BAD_REQUEST = 400;

    /**
     * 当前请求需要用户验证。
     * 该响应必须包含一个适用于被请求资源的 WWW-Authenticate 信息头用以询问用户信息。
     * 客户端可以重复提交一个包含恰当的 Authorization 头信息的请求。
     * 如果当前请求已经包含了 Authorization 证书，那么401响应代表着服务器验证已经拒绝了那些证书。
     * 如果401响应包含了与前一个响应相同的身份验证询问，且浏览器已经至少尝试了一次验证，那么浏览器应当向用户展示响应中包含的实体信息，
     * 因为这个实体信息中可能包含了相关诊断信息。参见RFC 2617。
     */
    int UNAUTHORIZED = 401;

    /**
     * 该状态码是为了将来可能的需求而预留的。
     */
    int PAYMENT_REQUIRED = 402;

    /**
     * 服务器已经理解请求，但是拒绝执行它。
     * 与401响应不同的是，身份验证并不能提供任何帮助，而且这个请求也不应该被重复提交。
     * 如果这不是一个 HEAD 请求，而且服务器希望能够讲清楚为何请求不能被执行，那么就应该在实体内描述拒绝的原因。
     * 当然服务器也可以返回一个404响应，假如它不希望让客户端获得任何信息。
     */
    int FORBIDDEN = 403;

    /**
     * 请求失败，请求所希望得到的资源未被在服务器上发现。
     * 没有信息能够告诉用户这个状况到底是暂时的还是永久的。
     * 假如服务器知道情况的话，应当使用410状态码来告知旧资源因为某些内部的配置机制问题，已经永久的不可用，而且没有任何可以跳转的地址。
     * 404这个状态码被广泛应用于当服务器不想揭示到底为何请求被拒绝或者没有其他适合的响应可用的情况下。
     * 出现这个错误的最有可能的原因是服务器端没有这个页面。
     */
    int NOT_FOUND = 404;

    /**
     * 请求行中指定的请求方法不能被用于请求相应的资源。该响应必须返回一个Allow 头信息用以表示出当前资源能够接受的请求方法的列表。
     * 鉴于 PUT，DELETE 方法会对服务器上的资源进行写操作，因而绝大部分的网页服务器都不支持或者在默认配置下不允许上述请求方法，对于此类请求均会返回405错误。
     */
    int BAD_METHOD = 405;

    /**
     * 请求的资源的内容特性无法满足请求头中的条件，因而无法生成响应实体。
     */
    int NOT_ACCEPTABLE = 406;

    /**
     * 与401响应类似，只不过客户端必须在代理服务器上进行身份验证。代理服务器必须返回一个 Proxy-Authenticate 用以进行身份询问。
     * 客户端可以返回一个 Proxy-Authorization 信息头用以验证。参见RFC 2617。
     */
    int PROXY_AUTHENTICATION_REQUIRED = 407;

    /**
     * 请求超时。客户端没有在服务器预备等待的时间内完成一个请求的发送。客户端可以随时再次提交这一请求而无需进行任何更改。
     */
    int REQUEST_TIMEOUT = 408;

    /**
     * 由于和被请求的资源的当前状态之间存在冲突，请求无法完成。
     * 这个代码只允许用在这样的情况下才能被使用：用户被认为能够解决冲突，并且会重新提交新的请求。
     * 该响应应当包含足够的信息以便用户发现冲突的源头。
     */
    int CONFLICT = 409;

    /**
     * 被请求的资源在服务器上已经不再可用，而且没有任何已知的转发地址。
     * 这样的状况应当被认为是永久性的。如果可能，拥有链接编辑功能的客户端应当在获得用户许可后删除所有指向这个地址的引用。
     * 如果服务器不知道或者无法确定这个状况是否是永久的，那么就应该使用404状态码。除非额外说明，否则这个响应是可缓存的。
     */
    int GONE = 410;

    /**
     * 服务器拒绝在没有定义 Content-Length 头的情况下接受请求。
     * 在添加了表明请求消息体长度的有效 Content-Length 头之后，客户端可以再次提交该请求。
     */
    int LENGTH_REQUIRED = 411;

    /**
     * 服务器在验证在请求的头字段中给出先决条件时，没能满足其中的一个或多个。
     * 这个状态码允许客户端在获取资源时在请求的元信息（请求头字段数据）中设置先决条件，以此避免该请求方法被应用到其希望的内容以外的资源上。
     */
    int PRECONDITION_FAILED = 412;

    /**
     * 服务器拒绝处理当前请求，因为该请求提交的实体数据大小超过了服务器愿意或者能够处理的范围。
     * 此种情况下，服务器可以关闭连接以免客户端继续发送此请求。
     */
    int REQUEST_ENTITY_TOO_LARGE = 413;

    /**
     * 请求的URI 长度超过了服务器能够解释的长度，因此服务器拒绝对该请求提供服务。
     *
     * 这比较少见，通常的情况包括：
     *
     * 1,本应使用POST方法的表单提交变成了GET方法，导致查询字符串（Query String）过长。
     *
     * 2,重定向URI “黑洞”，例如每次重定向把旧的 URI 作为新的 URI 的一部分，导致在若干次重定向后 URI 超长。
     *   客户端正在尝试利用某些服务器中存在的安全漏洞攻击服务器。
     *
     * 3,这类服务器使用固定长度的缓冲读取或操作请求的 URI，当 GET 后的参数超过某个数值后，可能会产生缓冲区溢出，导致任意代码被执行[1]。
     *   没有此类漏洞的服务器，应当返回414状态码。
     */
    int REQUEST_URI_TOO_LONG = 414;

    /**
     * 对于当前请求的方法和所请求的资源，请求中提交的实体并不是服务器中所支持的格式，因此请求被拒绝。
     */
    int UNSUPPORTED_TYPE = 415;

    /**
     * 如果请求中包含了 Range 请求头，并且 Range 中指定的任何数据范围都与当前资源的可用范围不重合，
     * 同时请求中又没有定义 If-Range 请求头，那么服务器就应当返回416状态码。
     */
    int REQUESTED_RANGE_NOT_SATISFIABLE = 416;

    /**
     * 在请求头 Expect 中指定的预期内容无法被服务器满足，或者这个服务器是一个代理服务器，
     * 它有明显的证据证明在当前路由的下一个节点上，Expect 的内容无法被满足。
     */
    int EXPECTATION_FAILED = 417;

    /**
     * 请求被指向到无法生成响应的服务器（比如由于连接重复使用）
     */
    int MISDIRECTED_REQUEST = 421;

    /**
     * 请求格式正确，但是由于含有语义错误，无法响应。（RFC 4918 WebDAV）
     */
    int UNPROCESSABLE_ENTITY = 422;

    /**
     * 当前资源被锁定。（RFC 4918 WebDAV）
     */
    int LOCKED = 423;

    /**
     * 由于之前的某个请求发生的错误，导致当前请求失败，例如 PROPPATCH。(RFC 4918 WebDAV)
     */
    int FAILED_DEPENDENCY = 424;

    /**
     * 状态码 425 Too Early 代表服务器不愿意冒风险来处理该请求，原因是处理该请求可能会被“重放”，从而造成潜在的重放攻击。(RFC 8470)
     */
    int TOO_EARLY = 425;

    /**
     * 客户端应当切换到TLS/1.0。(RFC 2817)
     */
    int UPGRADE_REQUIRED = 426;

    /**
     * 由微软扩展，代表请求应当在执行完适当的操作后进行重试。
     */
    int RETRY_WITH = 449;

    /**
     * 该请求因法律原因不可用。(RFC 7725)
     */
    int UNAVAILABLE_FOR_LEGAL_REASONS = 451;

    /* 这类状态码代表了服务器在处理请求的过程中有错误或者异常状态发生，也有可能是服务器意识到以当前的软硬件资源无法完成对请求的处理。
     * 除非这是一个HEAD 请求，否则服务器应当包含一个解释当前错误状态以及这个状况是临时的还是永久的解释信息实体。
     * 浏览器应当向用户展示任何在当前响应中被包含的实体。
     */
    /**
     * 服务器遇到了一个未曾预料的状况，导致了它无法完成对请求的处理。一般来说，这个问题都会在服务器端的源代码出现错误时出现。
     */
    int ERROR = 500;

    /**
     * 服务器不支持当前请求所需要的某个功能。当服务器无法识别请求的方法，并且无法支持其对任何资源的请求。
     */
    int NOT_IMPLEMENTED = 501;

    /**
     * 作为网关或者代理工作的服务器尝试执行请求时，从上游服务器接收到无效的响应。
     */
    int BAD_GATEWAY = 502;

    /**
     * 由于临时的服务器维护或者过载，服务器当前无法处理请求。这个状况是临时的，并且将在一段时间以后恢复。
     * 如果能够预计延迟时间，那么响应中可以包含一个 Retry-After 头用以标明这个延迟时间。
     * 如果没有给出这个 Retry-After 信息，那么客户端应当以处理500响应的方式处理它。
     * 注意：503状态码的存在并不意味着服务器在过载的时候必须使用它。某些服务器只不过是希望拒绝客户端的连接。
     */
    int SERVICE_UNAVAILABLE = 503;

    /**
     * 作为网关或者代理工作的服务器尝试执行请求时，未能及时从上游服务器（URI标识出的服务器，例如HTTP、FTP、LDAP）或者辅助服务器（例如DNS）收到响应。
     * 注意：某些代理服务器在DNS查询超时时会返回400或者500错误
     */
    int GATEWAY_TIMEOUT = 504;

    /**
     * 服务器不支持，或者拒绝支持在请求中使用的 HTTP 版本。
     * 这暗示着服务器不能或不愿使用与客户端相同的版本。响应中应当包含一个描述了为何版本不被支持以及服务器支持哪些协议的实体。
     */
    int HTTP_VERSION_NOT_SUPPORTED = 505;

    /**
     * 由《透明内容协商协议》（RFC 2295）扩展，代表服务器存在内部配置错误：被请求的协商变元资源被配置为在透明内容协商中使用自己，
     * 因此在一个协商处理中不是一个合适的重点。
     */
    int VARIANT_ALSO_NEGOTIATES = 506;

    /**
     * 服务器无法存储完成请求所必须的内容。这个状况被认为是临时的。WebDAV (RFC 4918)
     */
    int INSUFFICIENT_STORAGE = 507;

    /**
     * 服务器达到带宽限制。这不是一个官方的状态码，但是仍被广泛使用。
     */
    int BANDWIDTH_LIMIT_EXCEEDED = 509;

    /**
     * 获取资源所需要的策略并没有被满足。(RFC 2774)
     */
    int NOT_EXTENDED = 510;

    /**
     * 源站没有返回响应头部，只返回实体内容。
     */
    int UNPARSEABLE_RESPONSE_HEADERS = 600;
}
