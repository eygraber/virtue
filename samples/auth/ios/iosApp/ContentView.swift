import UIKit
import SwiftUI
import AuthSample

let virtueApp = AuthIosApplication()

struct ComposeView: UIViewControllerRepresentable {
    func makeUIViewController(context: Context) -> UIViewController {
        return virtueApp.createViewController()
    }

    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {}
}

struct ContentView: View {
    var body: some View {
        ComposeView().ignoresSafeArea(.all) // Compose UI can manage this
    }
}
