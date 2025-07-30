import SwiftUI
import Shared

struct ContentView: View {
    @State private var showContent = false
    var body: some View {
        VStack {
            Button(Demo.shared.buttonLabel) {
                withAnimation {
                    showContent = !showContent
                }
            }

            if showContent {
                VStack(spacing: 16) {
                    Image(systemName: "swift")
                        .font(.system(size: 200))
                        .foregroundColor(.accentColor)
                    Text("SwiftUI: \(Greeting().greet(greetingNumber: 1))")
                    Text("SwiftUI: \(Greeting().greet(greetingNumber: 2))")
                    Text("SwiftUI: \(Greeting().alternativeGreet(greetingNumber: 3))")
                }
                    .transition(.move(edge: .top).combined(with: .opacity))
            }

            Spacer()

            Text(Credits.shared.info)
                .foregroundColor(.gray)
        }
        .frame(maxWidth: .infinity, maxHeight: .infinity, alignment: .top)
        .padding()
    }
}

struct ContentView_Previews: PreviewProvider {
    static var previews: some View {
        ContentView()
    }
}
