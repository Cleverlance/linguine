import Foundation
import ObjectiveC

enum SampleLocaleOverride {
    // Set by the iOS CurrentLanguageControllerImpl while it resolves checklist rows.
    private static let threadDictionaryKey = "com.qinshift.linguine.sample.translationLanguage"

    private static let installed: Void = {
        let selector = NSSelectorFromString("currentLocale")
        guard let method = class_getClassMethod(NSLocale.self, selector) else {
            preconditionFailure("NSLocale.currentLocale is unavailable")
        }

        typealias CurrentLocale = @convention(c) (AnyClass, Selector) -> NSLocale
        let original = unsafeBitCast(method_getImplementation(method), to: CurrentLocale.self)
        let override: @convention(block) (AnyClass) -> NSLocale = { receiver in
            if let language = Thread.current.threadDictionary[threadDictionaryKey] as? String {
                return NSLocale(localeIdentifier: language)
            }
            return original(receiver, selector)
        }
        method_setImplementation(method, imp_implementationWithBlock(override))
    }()

    static func install() {
        _ = installed
    }
}
