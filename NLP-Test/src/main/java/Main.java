import opennlp.tools.namefind.NameFinderME;
import opennlp.tools.namefind.TokenNameFinderModel;
import opennlp.tools.parser.AbstractBottomUpParser;
import opennlp.tools.parser.Parse;
import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTagger;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.sentdetect.SentenceDetector;
import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;
import opennlp.tools.tokenize.Tokenizer;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;
import opennlp.tools.util.Span;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

public class Main {

    public static void main(String[] args) {
        Main main = new Main();

        try {
            main.run();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void run() throws IOException {
        String paragraph  = "O show é centrado principalmente em cinco personagens que vivem em Pasadena, Califórnia: o físico experimental Leonard Hofstadter e o físico teórico Sheldon Cooper, ambos vivendo juntos, partilhando um apartamento e trabalhando no Instituto de Tecnologia da Califórnia - Caltech; Penny, uma garçonete e aspirante a atriz que mais tarde se torna uma representante farmacêutica, e que vive como vizinha de ambos; e o engenheiro aeroespacial Howard Wolowitz e o astrofísico Rajesh Koothrappali, amigos e colegas de trabalho geeks semelhantes e socialmente desajeitados de Leonard e Sheldon. Os hábitos geeks e o intelecto dos quatro rapazes entra em contraste em relação ao efeito cômico com habilidades sociais e senso comum de Penny.";

        InputStream inputStream = getClass().getResourceAsStream("pt-sent.bin");
        SentenceModel sentenceModel = new SentenceModel(inputStream);
        inputStream.close();
        // the sentence detector and tokenizer constructors take paths to their respective models
        SentenceDetector sentenceDetector = new SentenceDetectorME(sentenceModel);

        InputStream inputStream1 = getClass().getResourceAsStream("pt-token.bin");
        TokenizerModel tokenizerModel = new TokenizerModel(inputStream1);
        inputStream1.close();
        Tokenizer tokenizer = new TokenizerME(tokenizerModel);

        // Extract sentences from the text
        String[] sentences = sentenceDetector.sentDetect(paragraph);
        System.out.println(sentences.length);
        System.out.println(Arrays.toString(sentences));

        System.out.println("===============================================================================");

        String sentence = sentences[0];
        // Get tokens in sentence
        String[] tokens = tokenizer.tokenize(sentence);
        System.out.println(sentence);
        System.out.println(Arrays.toString(tokens));

        System.out.println("===============================================================================");

        // Part-of-Speech Tagging
        InputStream inputStream2 = getClass().getResourceAsStream("pt-pos-maxent.bin");
        POSModel posModel = new POSModel(inputStream2);
        inputStream2.close();
        POSTagger posTagger = new POSTaggerME(posModel);
        String[] tags = posTagger.tag(tokens);
        System.out.println(Arrays.toString(tags));

        System.out.println("===============================================================================");

        // TODO: Arranjar uma maneira de encontrar nomes de classes e de individuos na ontologia... Se calhar há uma
        // maneira melhor de fazer isto (alternativa ao name finder? hash map?)
    }
}
