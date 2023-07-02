package xyz.hnkisdead.plugins.spot

import com.intellij.codeInsight.completion.*
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.patterns.ElementPattern
import com.intellij.patterns.PatternCondition
import com.intellij.patterns.PlatformPatterns
import com.intellij.psi.PsiElement
import com.intellij.psi.tree.TokenSet
import com.intellij.util.ProcessingContext
import org.jetbrains.yaml.YAMLElementTypes
import org.jetbrains.yaml.YAMLTokenTypes
import org.jetbrains.yaml.psi.YAMLFile

class SpotCompletionProver : CompletionProvider<CompletionParameters>() {
    override fun addCompletions(parameters: CompletionParameters, context: ProcessingContext, result: CompletionResultSet) {
        result.addElement(LookupElementBuilder.create("ssh_key"))
    }
}

class SpotFilePatternCondition : PatternCondition<PsiElement>("Spot File") {
    private val spotFiles = listOf("inventory.yml", "inventory.yaml", "spot.yml", "spot.yaml")

    override fun accepts(element: PsiElement, context: ProcessingContext?): Boolean {
        return element.containingFile is YAMLFile && spotFiles.contains(element.containingFile.name)
    }
}

class SpotCompletionContributor : CompletionContributor() {
    init {
        extend(CompletionType.BASIC, completionPlace(), SpotCompletionProver())
    }

    private fun completionPlace(): ElementPattern<out PsiElement> {
        val updated = PlatformPatterns.psiElement(YAMLTokenTypes.SCALAR_KEY);
        val inserted = PlatformPatterns.psiElement().withParent(
                PlatformPatterns.psiElement().withElementType(
                        TokenSet.create(YAMLElementTypes.SCALAR_PLAIN_VALUE, YAMLElementTypes.SCALAR_QUOTED_STRING)
                )
        )

        return PlatformPatterns.psiElement().andOr(inserted, updated).with(SpotFilePatternCondition())
    }
}